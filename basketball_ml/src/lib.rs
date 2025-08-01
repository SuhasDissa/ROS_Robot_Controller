use std::os::raw::{c_double, c_int};

const GRAVITY: f64 = 9.81; // m/s²
const BALL_DIAMETER: f64 = 0.24; // m
const HOOP_HEIGHT: f64 = 3.05; // m (standard basketball hoop)

#[repr(C)]
pub struct TrajectoryResult {
    pub angle: c_double,
    pub velocity: c_double,
    pub success: c_int, // 1 if calculation successful, 0 if failed
}

#[unsafe(no_mangle)]
pub extern "C" fn calculate_trajectory(
    distance: c_double,
    robot_height: c_double,
    target_height: c_double,
) -> TrajectoryResult {
    // Input validation
    if distance <= 0.0 || robot_height < 0.0 || target_height < 0.0 {
        return TrajectoryResult {
            angle: 0.0,
            velocity: 0.0,
            success: 0,
        };
    }

    // Calculate height difference
    let height_diff = target_height - robot_height;

    // Try different angles to find optimal trajectory
    let optimal = find_optimal_trajectory(distance, height_diff);

    match optimal {
        Some((angle, velocity)) => TrajectoryResult {
            angle,
            velocity,
            success: 1,
        },
        None => TrajectoryResult {
            angle: 0.0,
            velocity: 0.0,
            success: 0,
        },
    }
}

fn find_optimal_trajectory(distance: f64, height_diff: f64) -> Option<(f64, f64)> {
    let mut best_solution: Option<(f64, f64)> = None;
    let mut min_velocity = f64::INFINITY;

    // Test angles from 15° to 75° in 0.5° increments
    for angle_degrees in (150..=750).map(|x| x as f64 / 10.0) {
        let angle_rad = angle_degrees.to_radians();

        // Calculate required velocity using projectile motion equations
        let cos_angle = angle_rad.cos();
        let tan_angle = angle_rad.tan();

        // Avoid division by zero
        if cos_angle.abs() < 1e-10 {
            continue;
        }

        // Projectile motion: y = x*tan(θ) - (g*x²)/(2*v²*cos²(θ))
        // Solving for v: v² = (g*x²)/(2*cos²(θ)*(x*tan(θ) - y))
        let denominator = 2.0 * cos_angle.powi(2) * (distance * tan_angle - height_diff);

        if denominator <= 0.0 {
            continue;
        }

        let velocity_squared = (GRAVITY * distance.powi(2)) / denominator;

        if velocity_squared <= 0.0 {
            continue;
        }

        let velocity = velocity_squared.sqrt();

        // Check if this is a reasonable velocity (not too high)
        if velocity < 50.0 && velocity < min_velocity {
            // Verify the trajectory actually hits the target
            if verify_trajectory(distance, height_diff, angle_rad, velocity) {
                min_velocity = velocity;
                best_solution = Some((angle_degrees, velocity));
            }
        }
    }

    best_solution
}

fn verify_trajectory(distance: f64, height_diff: f64, angle_rad: f64, velocity: f64) -> bool {
    // Calculate time of flight to reach target distance
    let time = distance / (velocity * angle_rad.cos());

    // Calculate height at target distance
    let calculated_height = velocity * angle_rad.sin() * time - 0.5 * GRAVITY * time.powi(2);

    // Check if we hit the target within reasonable tolerance (10cm)
    (calculated_height - height_diff).abs() < 0.1
}

#[unsafe(no_mangle)]
pub extern "C" fn calculate_trajectory_with_arc(
    distance: c_double,
    robot_height: c_double,
    target_height: c_double,
    preferred_arc: c_int, // 0 = low arc, 1 = high arc
) -> TrajectoryResult {
    if distance <= 0.0 || robot_height < 0.0 || target_height < 0.0 {
        return TrajectoryResult {
            angle: 0.0,
            velocity: 0.0,
            success: 0,
        };
    }

    let height_diff = target_height - robot_height;

    let angle_range = if preferred_arc == 0 {
        // Low arc: 15° to 45°
        150..=450
    } else {
        // High arc: 45° to 75°
        450..=750
    };
    let angle_range = angle_range.map(|x| x as f64 / 10.0);

    let mut best_solution: Option<(f64, f64)> = None;
    let mut min_velocity = f64::INFINITY;

    for angle_degrees in angle_range {
        let angle_rad = angle_degrees.to_radians();
        let cos_angle = angle_rad.cos();
        let tan_angle = angle_rad.tan();

        if cos_angle.abs() < 1e-10 {
            continue;
        }

        let denominator = 2.0 * cos_angle.powi(2) * (distance * tan_angle - height_diff);

        if denominator <= 0.0 {
            continue;
        }

        let velocity_squared = (GRAVITY * distance.powi(2)) / denominator;

        if velocity_squared <= 0.0 {
            continue;
        }

        let velocity = velocity_squared.sqrt();

        if velocity < 50.0 && velocity < min_velocity {
            if verify_trajectory(distance, height_diff, angle_rad, velocity) {
                min_velocity = velocity;
                best_solution = Some((angle_degrees, velocity));
            }
        }
    }

    match best_solution {
        Some((angle, velocity)) => TrajectoryResult {
            angle,
            velocity,
            success: 1,
        },
        None => TrajectoryResult {
            angle: 0.0,
            velocity: 0.0,
            success: 0,
        },
    }
}

// Helper function for testing trajectories
#[unsafe(no_mangle)]
pub extern "C" fn simulate_shot(
    initial_velocity: c_double,
    angle_degrees: c_double,
    robot_height: c_double,
    target_distance: c_double,
) -> c_double {
    let angle_rad = angle_degrees.to_radians();
    let time = target_distance / (initial_velocity * angle_rad.cos());

    robot_height + initial_velocity * angle_rad.sin() * time - 0.5 * GRAVITY * time.powi(2)
}