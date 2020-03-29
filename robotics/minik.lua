----------------------------------------------
--          Bogazici University             --
--     Electrical and Electronics Dep.      --
--                                          --
-- EE451 - Introduction to Robot Control    --
-- -------------------------------------------       
 
if (sim_call_type==sim.syscb_init) then
 
    -- handles to robot, motors, package locs etc..
 
    robotHandle = sim.getObjectHandle('base')
    leftMotor = sim.getObjectHandle('jointLeft')
    rightMotor = sim.getObjectHandle('jointRight')
    baseAreaHandle = sim.getObjectHandle('baseArea')
    leftSensorHandle = sim.getObjectHandle('sensorLeft')
    rightSensorHandle = sim.getObjectHandle('sensorRight')
    goalHandle = sim.getObjectHandle('Goal')

    obstacleHandles={-1,-1,-1}
    obstacleHandles[1] = sim.getObjectHandle('obstacle1')
    obstacleHandles[2] = sim.getObjectHandle('obstacle2')
    obstacleHandles[3] = sim.getObjectHandle('obstacle3')
 
    robotRad = 0.1                    -- robot radius in m
    obstacleRad = {0.2,0.2,0.2}                 -- obstacle radius in m
    vMax = 0.2                        -- max trans vel in m/sec
    wMax = math.pi/2                  -- max rot vel in rad/sec
 
    eps = 0.01                        -- stop threshold in m
    init_degree = 0
    axleLen = 0.2                     -- Axle lenght in m
    wheelRad = 0.045                  -- Wheel radius in m
    wheelCirc = 2*math.pi*wheelRad    -- Wheel circumference
    robotCirc = math.pi*axleLen       -- Topview robot circumference
   
    -- Simulation start time
    startTime = sim.getSimulationTime()
    oldRobotPos = sim.getObjectPosition(robotHandle, -1)
    check = 0
    count = 0
    count2 = 0
    count3 = 0
    count4 = 0
    count5 = 0
    count7 = 0
    count8 = 0
    goalfound = 0
    size = 0
    dummyhandle={-1}
    dist = 10
    
    sim.addStatusbarMessage("Program is started")   

end
 
function sign(x)
  return x>0 and 1 or x<0 and -1 or 0
end

function calcGrad(goalPosition,robotPosition,obstaclePositions,eta)
    dobs = {{},{},{-1,-1}}
    db = {{},{},{}}
    B = {{},{},{}}
    -- Initialize forces
    Fx = 0                                                                                       
    Fy = 0                                                                                                                                                                                                        
    
    dgx = robotPosition[1] - goalPosition[1]
    dgy = robotPosition[2] - goalPosition[2]
    gamma = dgx*dgx+dgy*dgy
    dgammax = 2*dgx
    dgammay = 2*dgy
    BB = 1
    for i=1,table.getn(obstaclePositions),1 do
        if obstaclePositions[i] ~= -1 then
            size = size+1
        end
    end
    for i=1,size,1 do
        dobs[i][1] = robotPosition[1] - obstaclePositions[i][1]
        dobs[i][2] = robotPosition[2] - obstaclePositions[i][2]
        db[i][1] = 2*dobs[i][1]
        db[i][2] = 2*dobs[i][2]
        B[i] = dobs[i][1]*dobs[i][1]+dobs[i][2]*dobs[i][2] - (robotRad+obstacleRad[i])*(robotRad+obstacleRad[i])
        BB = BB*B[i]
        
    end
    size=0
    -- TODO: Calculate the gradient
    if dobs[3][1] == -1 then
        k = 3
        Fx = (k*math.pow(gamma,k-1)*dgammax*BB - math.pow(gamma,k)*(db[1][1]*B[2]+db[2][1]*B[1]))/(BB*BB)
        Fy = (k*math.pow(gamma,k-1)*dgammay*BB - math.pow(gamma,k)*(db[1][2]*B[2]+db[2][2]*B[1]))/(BB*BB)
    else
        k = 5
        Fx = (k*math.pow(gamma,k-1)*dgammax*BB - math.pow(gamma,k)*(db[1][1]*B[2]*B[3]+db[2][1]*B[1]*B[3]+db[3][1]*B[2]*B[1]))/(BB*BB)
        Fy = (k*math.pow(gamma,k-1)*dgammay*BB - math.pow(gamma,k)*(db[1][2]*B[2]*B[3]+db[2][2]*B[1]*B[3]+db[3][2]*B[2]*B[1]))/(BB*BB)
    end
    
    return -Fx,-Fy
    
end
 
-- Set robot speed in translational velocity in m/sec
-- and rotational velocity in rad/sec
function setRobotSpeed(transVel, rotVel)
    -- Using same motor placement configuration as in Minik robots:
    -- Left motor turns forward for positive speed values
    -- Right motor turns backward for positive speed values
 
    -- Convert speed to rad/sec
    transVel = transVel/wheelCirc * 2 * math.pi;
    rotVel = rotVel * (robotCirc/wheelCirc);
    -- Give speed to both left and right motor
    leftMotorSpeed = transVel - rotVel
    rightMotorSpeed = -1*transVel - rotVel
 
    -- Set the limits (DO NOT CHANGE THIS PART)
 
    tau_max = 5
 
    if math.abs(leftMotorSpeed) > tau_max then
        leftMotorSpeed = sign(leftMotorSpeed) * tau_max
    end
 
    if math.abs(rightMotorSpeed) > tau_max then
        rightMotorSpeed = sign(rightMotorSpeed) * tau_max
    end
       
    sim.setJointTargetVelocity(leftMotor,leftMotorSpeed)
    sim.setJointTargetVelocity(rightMotor,rightMotorSpeed)

end
 
function orderingobstacles(obstaclePositions,robotPosition)
                                                                                                                                                                                      
    
    dox1 = robotPosition[1] - obstaclePositions[1][1]
    doy1 = robotPosition[2] - obstaclePositions[1][2]
    dox2 = robotPosition[1] - obstaclePositions[2][1]
    doy2 = robotPosition[2] - obstaclePositions[2][2]
    dox3 = robotPosition[1] - obstaclePositions[3][1]
    doy3 = robotPosition[2] - obstaclePositions[3][2]
    gamma1 = dox1*dox1+doy1*doy1 --robot distances to obstacles
    gamma2 = dox2*dox2+doy2*doy2
    gamma3 = dox3*dox3+doy3*doy3

    if gamma3 >= gamma2 and  gamma3 >= gamma1 then
        minmidmax11 = obstaclePositions[1][1]
        minmidmax12 = obstaclePositions[1][2]
        minmidmax21 = obstaclePositions[2][1]
        minmidmax22 = obstaclePositions[2][2]
        minmidmax31 = obstaclePositions[3][1]
        minmidmax32 = obstaclePositions[3][2]
    end         
    if gamma2 >= gamma1 and gamma2 >= gamma3 then
        minmidmax11 = obstaclePositions[1][1]
        minmidmax12 = obstaclePositions[1][2]
        minmidmax21 = obstaclePositions[3][1]
        minmidmax22 = obstaclePositions[3][2]
        minmidmax31 = obstaclePositions[2][1]
        minmidmax32 = obstaclePositions[2][2]
    end
    if gamma1 >= gamma3 and gamma1 >= gamma2 then
        minmidmax11 = obstaclePositions[2][1]
        minmidmax12 = obstaclePositions[2][2]
        minmidmax21 = obstaclePositions[3][1]
        minmidmax22 = obstaclePositions[3][2]
        minmidmax31 = obstaclePositions[1][1]
        minmidmax32 = obstaclePositions[1][2]
    end


    obstaclePositions[1][1]=minmidmax11
    obstaclePositions[1][2]=minmidmax12
    obstaclePositions[2][1]=minmidmax21
    obstaclePositions[2][2]=minmidmax22
    obstaclePositions[3][1]=minmidmax31
    obstaclePositions[3][2]=minmidmax32
  
    sim.addStatusbarMessage(obstaclePositions[1][1])
    sim.addStatusbarMessage(obstaclePositions[1][2])
    sim.addStatusbarMessage(obstaclePositions[2][1])
    sim.addStatusbarMessage(obstaclePositions[2][2])
    sim.addStatusbarMessage(obstaclePositions[3][1])
    sim.addStatusbarMessage(obstaclePositions[3][2])
    size=0
    return obstaclePositions[1][1],obstaclePositions[1][2],obstaclePositions[2][1],obstaclePositions[2][2],obstaclePositions[3][1],obstaclePositions[3][2]
    
end



 
 
if (sim_call_type==sim.syscb_actuation) then
    
        -- Get robot pose
    
    robotPosition = sim.getObjectPosition(robotHandle, -1)
    robotOrientation = sim.getObjectOrientation(robotHandle, -1)
    SensorPosition = sim.getObjectPosition(leftSensorHandle, -1)
    if init_degree < 360 then
        setRobotSpeed(0,3)
        init_degree = init_degree + 6
        prox,sensdist,sensPos,sensHandle = sim.readProximitySensor(leftSensorHandle)
        if prox == 1 and count == 0  then
            obstaclePositions= {-1,-1,-1}
            obstaclePositions[1] = sim.getObjectPosition(sensHandle,-1)
            count = count + 1
            sim.addStatusbarMessage('obstacle1 is found')
        end
        if prox == 0 and count > 0  then
            count2 = count2 + 1
        end
        if prox == 1 and  count2 > 0 and count3 == 0 then
            obstaclePositions[2] = sim.getObjectPosition(sensHandle,-1)
            count3 = count3 +1 
            sim.addStatusbarMessage('obstacle2 is found')
        end

        if prox == 0 and count3 > 0  then
            count7 = count7 + 1
        end


        if prox == 1 and  count7 > 0 and count8 == 0 then   --to check 3 obstacles at first turn
            obstaclePositions[3] = sim.getObjectPosition(sensHandle,-1)
            count8 = count8 + 1
            sim.addStatusbarMessage('obstacle3 is found')
        end
        if count8 == 1 then
            obstaclePositions[1][1],obstaclePositions[1][2],obstaclePositions[2][1],obstaclePositions[2][2],obstaclePositions[3][1],obstaclePositions[3][2] = orderingobstacles(obstaclePositions,robotPosition)
            sim.addStatusbarMessage('orderedbebisim')
        end

    
    else
        gprox,gsensdist,gsensPos,gsensHandle = sim.readProximitySensor(rightSensorHandle)
        if gprox == 1 and goalfound == 0  then
            goalPosition = sim.getObjectPosition(gsensHandle,-1)
            goalfound = goalfound + 1
            sim.addStatusbarMessage('goal is found')
        end
        if obstaclePositions[3] == -1 then
            sim.addStatusbarMessage('I am searching for 3rd obstacle')
        end

    
        robotTh = robotOrientation[3] + math.pi/2
    
        -- Get goal pose
        --goalPosition = sim.getObjectPosition(goalHandle,-1)
        if goalfound > 0 then
            dgx = robotPosition[1] - goalPosition[1]
            dgy = robotPosition[2] - goalPosition[2]
            dist = dgx*dgx+dgy*dgy
        end
        -- Calculate middle of obstacles pos
        obsmidPosition = {}
        obsmidPosition[1] = (obstaclePositions[1][1] + obstaclePositions[2][1])/2
        obsmidPosition[2] = (obstaclePositions[1][2] + obstaclePositions[2][2])/2
            --sim.addStatusbarMessage('HERE ARE THE X VALUES OF CLOSEST 2')
            --sim.addStatusbarMessage(obstaclePositions[1][1])
            --sim.addStatusbarMessage(obstaclePositions[1][2])
            --sim.addStatusbarMessage('HERE IS THE X VALUE OF THE MIDDLE')
            --sim.addStatusbarMessage(obsmidPosition[1])
            --sim.addStatusbarMessage('HERE ARE THE Y VALUES OF CLOSEST 2')
            --sim.addStatusbarMessage(obstaclePositions[2][1])
            --sim.addStatusbarMessage(obstaclePositions[2][1])
            --sim.addStatusbarMessage('HERE IS THE Y VALUE OF THE MIDDLE')
            --sim.addStatusbarMessage(obsmidPosition[2])
            --if count5==1 or count8 == 1 then
            --sim.addStatusbarMessage('HERE IS THE POSITION OF THE 3rd Obstacle')
            --sim.addStatusbarMessage(obstaclePositions[3][1])
            --sim.addStatusbarMessage(obstaclePositions[3][2])
            --end
            
        dmx = robotPosition[1] - obsmidPosition[1]
        dmy = robotPosition[2] - obsmidPosition[2]
        middist = (dmx*dmx)+(dmy*dmy)
        -- Calculate gradient
        if check == 0 then
            Fx,Fy = calcGrad(obsmidPosition,robotPosition,obstaclePositions,eta)
            sim.addStatusbarMessage('I am going to the middle')
        else
            prox,sensdist,sensPos,sensHandle = sim.readProximitySensor(leftSensorHandle)
                
            if prox == 0 and count3 > 0  then
                count4 = count4 + 1
            end
            if prox == 1 and  count4 > 0 and count5 == 0 and count8 == 0  then
                sim.addStatusbarMessage('I am trying to find the 3rd obstacle')
                dummyhandle=sim.getObjectPosition(sensHandle,-1)
                if dummyhandle[1] ~= obstaclePositions[1][1] and dummyhandle[2] ~= obstaclePositions[1][2] and dummyhandle[1] ~= obstaclePositions[2][1] and dummyhandle[2] ~= obstaclePositions[2][2] then
                    obstaclePositions[3] = sim.getObjectPosition(sensHandle,-1)
                    count5 = count5 +1
                    sim.addStatusbarMessage('I found the 3rd obstacle')
                end
            end    

            Fx,Fy = calcGrad(goalPosition,robotPosition,obstaclePositions,eta)

        end
        
        -- TODO: Calculate target velocity
        v=0
        w=0
            
        Fmag = math.sqrt(Fx*Fx+Fy*Fy)
        Fth = math.atan2(Fy,Fx)
        
        th = Fth - robotTh
        
        v = vMax * math.cos(th)
        w = wMax * math.sin(th)
        if middist < 0.01 then
            check = check +1
            sim.addStatusbarMessage(' I am going to the Goal')
        end
        if dist < 0.005 then
            setRobotSpeed(0,0)
        else
            setRobotSpeed(v,w)
        end
    end
    
    
    
end
 
if (sim_call_type==sim.syscb_sensing) then
 
    -- Put some sensing code here
 
end
 
 
if (sim_call_type==sim.syscb_cleanup) then
 
    -- Put some restoration code here
 
end