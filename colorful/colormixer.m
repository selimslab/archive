% Always instert these lines 
% ----------------------------------------------------------------------- %
clear variables;
close all;
clc;
% ----------------------------------------------------------------------- %


% Import the .csv data for LEDs and assign arrays to each one 
% Here we are using 5 LEDs, so there will be 5 arrays 
% .csv files should have no spaces in the filename, and should not have a
% number to start the filename
% The .csv file should have wavelength in nm in one column, and mW/nm in 
% the second column
% ----------------------------------------------------------------------- %
% Making an array A for Amber LED, and copying the .csv to the array
amber = dlmread('Amber.csv');

% Making an array G for Green LED, and copying the .csv to the array
green = dlmread('Green.csv');

% Making an array B for Blue LED, and copying the .csv to the array
blue = dlmread('Blue.csv');

% Making an array w for White 2500K LED, and copying the .csv to the array
white_2500K = dlmread('White_2500K.csv');

% Making an array W for White 6500K LED, and copying the .csv to the array
white_6500K = dlmread('White_6500K.csv');
% ----------------------------------------------------------------------- %


% ----------------------------------------------------------------------- %
% Making an array for the standard tristimulus values
% First column is nm, second third and fourth are x, y z respectively 
standard_tri_values = dlmread('standard_tristimulus_values.csv');
% ----------------------------------------------------------------------- %



% ----------------------------------------------------------------------- %
% To make calculations easier, we are going to import the power values for
% each LED and give them their own array. These are also from 360nm-780nm
amber_power_values = dlmread('Amber_power_values.csv');
blue_power_values = dlmread('Blue_power_values.csv');
green_power_values = dlmread('Green_power_values.csv');
white_2500K_power_values = dlmread('white_2500K_power_values.csv');
white_6500K_power_values = dlmread('white_6500K_power_values.csv');
% ----------------------------------------------------------------------- %


% ----------------------------------------------------------------------- %
% We are also going to import the x, y and z values for the standard
% tristimulus values, each getting their own array
standard_tri_values_x = dlmread('standard_tri_values_x.csv');
standard_tri_values_y = dlmread('standard_tri_values_y.csv');
standard_tri_values_z = dlmread('standard_tri_values_z.csv');
% ----------------------------------------------------------------------- %


% ----------------------------------------------------------------------- %
% We are going to import the x and y coordinates of the colour temperatures
% on the black body curve, going from 1800K - 15990K, in steps of 110K.
x_coordinates_of_black_body_curve = dlmread('x_coordinates_of_black_body_curve.csv');
y_coordinates_of_black_body_curve = dlmread('y_coordinates_of_black_body_curve.csv');
xy_coordinates_of_black_body_curve = dlmread('xy_coordinates_of_black_body_curve.csv');
% ------------------------------------



% ----------------------------------------------------------------------- %
% Now we are going to calculate X, Y and Z for each LED. This is done by
% multiplying each power value for the LED by the standard tristimulus
% value at that particular wavelength for either x, y or z. Each value is
% stored in a new array, and then these values are all added together to
% get either X, Y or Z

% eg at 360nm,
% the power of the white_2500K LED is 1.06e-07. at 360nm, the power of the
% standard tristimulus value (for x) is 0. So the first value in the new
% array would be (1.06e-07 * 0). This process is repeated for every
% wavelength (1nm steps) until the last step of 780nm. Then, each value in
% this array is added together to get X for the white_2500K LED.

% Since we have two 421x1 arrays, and we are doing element by element
% multiplication, we have to use the (.*) function, and not just normal
% matrix multiplication.

X_array_white_2500K = white_2500K_power_values.*standard_tri_values_x;
X_white_2500K = sum(X_array_white_2500K);

Y_array_white_2500K = white_2500K_power_values.*standard_tri_values_y;
Y_white_2500K = sum(Y_array_white_2500K);

Z_array_white_2500K = white_2500K_power_values.*standard_tri_values_z;
Z_white_2500K = sum(Z_array_white_2500K);


X_array_white_6500K = white_6500K_power_values.*standard_tri_values_x;
X_white_6500K = sum(X_array_white_6500K);

Y_array_white_6500K = white_6500K_power_values.*standard_tri_values_y;
Y_white_6500K = sum(Y_array_white_6500K);

Z_array_white_6500K = white_6500K_power_values.*standard_tri_values_z;
Z_white_6500K = sum(Z_array_white_6500K);


X_array_amber = amber_power_values.*standard_tri_values_x;
X_amber = sum(X_array_amber);

Y_array_amber = amber_power_values.*standard_tri_values_y;
Y_amber = sum(Y_array_amber);

Z_array_amber = amber_power_values.*standard_tri_values_z;
Z_amber = sum(Z_array_amber);


X_array_green = green_power_values.*standard_tri_values_x;
X_green = sum(X_array_green);

Y_array_green = green_power_values.*standard_tri_values_y;
Y_green = sum(Y_array_green);

Z_array_green = green_power_values.*standard_tri_values_z;
Z_green = sum(Z_array_green);


X_array_blue = blue_power_values.*standard_tri_values_x;
X_blue = sum(X_array_blue);

Y_array_blue = blue_power_values.*standard_tri_values_y;
Y_blue = sum(Y_array_blue);

Z_array_blue = blue_power_values.*standard_tri_values_z;
Z_blue = sum(Z_array_blue);
% ----------------------------------------------------------------------- %




% ----------------------------------------------------------------------- %
% Now we are going to calculate the CIE 1931 Chromaticity coordinates for
% each LED. Each LED has a specified point on this diagram, x and y.
% eg. for x, we do X/(X+Y+Z). for y, we do Y/(X+Y+Z)

x_white_2500K = X_white_2500K/(X_white_2500K + Y_white_2500K + Z_white_2500K);
y_white_2500K = Y_white_2500K/(X_white_2500K + Y_white_2500K + Z_white_2500K);

x_white_6500K = X_white_6500K/(X_white_6500K + Y_white_6500K + Z_white_6500K);
y_white_6500K = Y_white_6500K/(X_white_6500K + Y_white_6500K + Z_white_6500K);

x_amber = X_amber / (X_amber + Y_amber + Z_amber);
y_amber = Y_amber / (X_amber + Y_amber + Z_amber);

x_green = X_green / (X_green + Y_green + Z_green);
y_green = Y_green / (X_green + Y_green + Z_green);

x_blue = X_blue / (X_blue + Y_blue + Z_blue);
y_blue = Y_blue / (X_blue + Y_blue + Z_blue);
% ----------------------------------------------------------------------- %



% ----------------------------------------------------------------------- %
% Now we have x, y for every LED. In the following calculations, we are
% going to input the x, y for the point we would like to achieve. It will
% be referred to as x_mix, y_mix. We are also going to input Y_mix, which is
% how bright we would like that colour to shine. The program will give us
% an output of Yamber, Yblue, Ygreen, Ywhite2500K and Ywhite6500K. These
% are how bright we have to shine the 5 LEDs in order to attain xmix, ymix
% at Ymix.

% Firstly, type in your x_mix here. For our example we will use 0.3, 0.33
my_file =csvread(xy_coordinates_of_black_body_curve.csv)
[d1,d2] = size(my_file)
for x_val = 1:d1
    for y_val = 1:d1
       x_mix = my_file(x_val,1);
       y_mix = my_file(y_val,2);

% Secondly, type in your Y_mix here. For our example we will use 40 lumens.
Y_mix = 40;
% ----------------------------------------------------------------------- %


% ----------------------------------------------------------------------- %
% Now, to get the brightnesses for each LED to get x_mix, y_mix at Y_mix we
% do the following. 

% First, set up a 3x5 Matrix, called A
A = [((x_amber - x_mix)/y_amber) ((x_green - x_mix)/y_green) ((x_blue - x_mix)/y_blue) ((x_white_2500K - x_mix)/y_white_2500K) ((x_white_6500K - x_mix)/y_white_6500K)

     ((y_amber - y_mix)/y_amber) ((y_green - y_mix)/y_green) ((y_blue - y_mix)/y_blue) ((y_white_2500K - y_mix)/y_white_2500K) ((y_white_6500K - y_mix)/y_white_6500K) 
     
     1 1 1 1 1];
 
% Then, take the pseudo inverse of this matrix and call the resulting
% Matrix B

B = pinv(A);

% Finally, we multiply B by another matrix. The results are stored in
% matrix C. 
% C = [Y_amber; Y_green; Y_blue; Y_white_2500K; Y_white_6500K]

C = B*[0; 0; Y_mix];

C_array(y_val]


    end
end










