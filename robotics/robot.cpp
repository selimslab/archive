#include <iostream>
#include <tuple>
#include <cmath>


const double PI  =3.141592653589793238463;



using namespace std;


  class RobotController {
    float robotRadius = 0.1;

    float obstacleRadius = 0.2;

    float vMax = 0.2;
    float wMax = PI / 2;

    float epsilon = 0.01;
    int initial_degree = 0;

    float axleLength = 0.2;
    float wheelRadius = 0.045;
    float wheelCircumference = 2 * PI * wheelRadius;
    float topViewRobotCircumference = PI * axleLength;

    float obstaclePositions[3][3];
    float robotPosition[3] = {0, 0, 0};
    float robotOrientation[3];

    bool leftCameraDetectedObstacle;
    float distanceLeft;
    float leftPosition[];

    bool rightCameraDetectedObstacle;
    float distanceRight;
    float rightPosition[];

    tuple<float, float> subtract_arrays(float x[], float y[]) {
      float dx = x[0] - y[0];
      float dy = x[1] - y[1];
      return  make_tuple(dx,dy);
    }

    float euclidean_distance(float dx,float dy){
        return dx * dx + dy * dy;
    }

    float get_distance(float currentPosition[], float distantPosition[]) {
      float dx,dy;
      tie(dx, dy) = subtract_arrays(currentPosition, distantPosition);
      float distance = euclidean_distance(dx,dy);
      return distance;
    }

    public: void go() {
     bool is_first_obstacle = true;
      if (initial_degree < 360) {
        setRobotSpeed(0, 3);
        initial_degree = initial_degree + 6;

        if (leftCameraDetectedObstacle == true and is_first_obstacle == true) {
          obstaclePositions[0][0] = leftPosition[0] + robotPosition[0];
          obstaclePositions[0][1] = leftPosition[1] + robotPosition[1];
          obstaclePositions[0][2] = leftPosition[2] + robotPosition[2];

          is_first_obstacle = false ;
        }

      } else {

        float robotTheta = robotOrientation[3] + PI / 2;

        // Get from cameras
        float goalPosition[3];
        float obstaclePositions[3][3];

        float distance_to_goal = get_distance(robotPosition, goalPosition);

        float position_of_middle_of_obstacles[3];
        float distance_from_middle_of_obstacles = get_distance(robotPosition, position_of_middle_of_obstacles);

        bool any_obstacle = true;
        float Fx,Fy;
        
        
        if (any_obstacle == true) {
          tie(Fx,Fy) = calculateGradient(position_of_middle_of_obstacles, robotPosition, obstaclePositions);
        
        } else {
          tie(Fx,Fy) = calculateGradient(goalPosition, robotPosition, obstaclePositions);
        }

        float v = 0;
        float w = 0;

        float Fmag = sqrt(Fx * Fx + Fy * Fy);
        float Fth = atan2(Fy, Fx);

        float th = Fth - robotTheta;

        v = vMax * cos(th);
        w = wMax * sin(th);

        if (distance_from_middle_of_obstacles < epsilon) {
          any_obstacle = false;
        }

        if (distance_to_goal < epsilon) {
          setRobotSpeed(0, 0);
        } else {
          setRobotSpeed(v, w);
        }

      }

    }

    bool sign(int x) {
      return (x > 0 and 1) or(x < 0 and - 1) or 0;
    }


    tuple<float, float> calculateGradient(float goalPosition[], float robotPosition[], float obstaclePositions[3][3] ) {
      float Fx = 0;
      float Fy = 0;

      
      float dgx, dgy, do1x, do1y, do2x, do2y, do3x, do3y;

      tie(dgx, dgy) = subtract_arrays(robotPosition, goalPosition);

      tie(do1x, do1y) = subtract_arrays(robotPosition, obstaclePositions[0]);

      tie(do2x, do2y) = subtract_arrays(robotPosition, obstaclePositions[1]);

      tie(do3x, do3y) = subtract_arrays(robotPosition, obstaclePositions[2]);

      float gamma = dgx * dgx + dgy * dgy;

      float B1 = do1x * do1x + do1y * do1y - (robotRadius + obstacleRadius) * (robotRadius + obstacleRadius);
      float B2 = do2x * do2x + do2y * do2y - (robotRadius + obstacleRadius) * (robotRadius + obstacleRadius);
      float B3 = do3x * do3x + do3y * do3y - (robotRadius + obstacleRadius) * (robotRadius + obstacleRadius);

      float B = B1 * B2 * B3;

      int k = 5;
      Fx = (k * pow(gamma, k - 1) * 2 * dgx * B - pow(gamma, k) * (2 * do1x * B2 * B3 + 2 * do2x * B1 * B3 + 2 * do3x * B2 * B1)) / (B * B);
      Fy = (k * pow(gamma, k - 1) * 2 * dgy * B - pow(gamma, k) * (2 * do1y * B2 * B3 + 2 * do2y * B1 * B3 + 2 * do3y * B2 * B1)) / (B * B);

      return make_tuple(-Fx, -Fy);

    }



    void setRobotSpeed(float transVel, float rotVel) {
      // Convert speed to rad/sec
      transVel = transVel / wheelCircumference * 2 * PI;
      rotVel = rotVel * (topViewRobotCircumference / wheelCircumference);

      // Give speed to both left and right motor
      float leftMotorSpeed = transVel - rotVel;
      float rightMotorSpeed = -transVel - rotVel;

      int tau_max = 5;

      if (abs(leftMotorSpeed) > tau_max) {
        leftMotorSpeed = sign(leftMotorSpeed) * tau_max;
      }

      if (abs(rightMotorSpeed) > tau_max) {
        rightMotorSpeed = sign(rightMotorSpeed) * tau_max;
      }
    }

  };



int main() {
  std::cout << "Hello World!\n";
  RobotController controller = RobotController();
  controller.go();

}