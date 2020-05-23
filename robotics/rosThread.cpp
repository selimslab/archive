#include "rosThread.h"

#include <iostream>
#include <cmath>
#include <vector>
#include <unistd.h>
#include <math.h>


using namespace std;
RosThread::RosThread()
{
    robotX = 0;
    robotY = 0;
    robotTh = 0;

    targetX = 3;
    targetY = 3;

    travelDistance = 0;

    completed=0;

    Obstacle temp1;

    temp1.x = 1;
    temp1.y = 1;
    temp1.r = 0.2;

    obstacles.push_back(temp1);


}

RosThread::~RosThread()
{
}

void RosThread::work(){

    velPub = n.advertise<minik_ros_wrapper::minikSetVelocityMsg>("minik_ros_wrapper/minikSetVelocityMsg", 1);
    odomSub = n.subscribe("odom", 1, &RosThread::odomHandler, this);
    targetSub = n.subscribe("target", 1, &RosThread::targetHandler, this);
    obstacleSub = n.subscribe("obstacles", 1, &RosThread::obstacleHandler, this);

    ros::Rate loop(10);
    while(ros::ok())
    {

        demoLoop();
        ros::spinOnce();
        loop.sleep();
    }

    qDebug() << "Quitting";
    ros::shutdown();
}

void RosThread::demoLoop()
{
    sendVelocityCommand(0.2,-0.2);
    cout << "X: " << robotX << " \t Y: " << robotY << " \t Theta: " << robotTh << endl;
    if (obstacles.size() > 0){
        cout << "oX: " << obstacles[0].x << " \t oY: " << obstacles[0].y << " \t oR: " << obstacles[0].r << endl;
    }
}


void RosThread::odomHandler(const nav_msgs::OdometryConstPtr &odomMsg){

    //  ^ Y
    //  |				<-- Th
    //  |			      |
    //   -----> X   	
    robotX = odomMsg->pose.pose.position.x;
    robotY = odomMsg->pose.pose.position.y;

    tf::Quaternion q(odomMsg->pose.pose.orientation.x, odomMsg->pose.pose.orientation.y,
                     odomMsg->pose.pose.orientation.z, odomMsg->pose.pose.orientation.w);
    tf::Matrix3x3 m(q);
    double roll, pitch, yaw;
    m.getRPY(roll, pitch, yaw);

    robotTh = yaw;

    travelDistance += sqrt(pow(robotX - _lastX, 2) + pow(robotY - _lastY, 2));
    _lastX = robotX;
    _lastY = robotY;
}

void RosThread::targetHandler(const geometry_msgs::PoseConstPtr &targetMsg){

    targetX = targetMsg->position.x;
    targetY = targetMsg->position.y;

}

void RosThread::obstacleHandler(const std_msgs::Float32MultiArrayConstPtr &obstacleMsg){

    obstacles.clear();

    for (int i=0; i<obstacleMsg->layout.dim[0].size; i++){

        Obstacle temp;

        temp.x = obstacleMsg->data[i*3];
        temp.y = obstacleMsg->data[i*3+1];
        temp.r = obstacleMsg->data[i*3+2];

        obstacles.push_back(temp);
    }

}

void RosThread::sendVelocityCommand(double leftWheel, double rightWheel){
    int leftTick = leftWheel * ticks_per_meter;
    int rightTick = rightWheel * ticks_per_meter;

    minik_ros_wrapper::minikSetVelocityMsg msg;

    vector<int> motorID;
    motorID.push_back(0);
    motorID.push_back(1);
    msg.motorID = motorID;

    vector<int> velocity;
    velocity.push_back(leftTick);
    velocity.push_back(rightTick);
    msg.velocity = velocity;

    velPub.publish(msg);
}
