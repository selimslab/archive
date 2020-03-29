clear
clc
close all

% define parameters
mu1 = [3; 3];
mu2 = [-3 ; -3];

sigma = 3;

cov1 = (sigma^2) .* [1 0; 0 1 ];
cov2 = (sigma^2) .* [1 0; 0 1 ];




if isfile('T2.mat')
    load D1.mat
    load T1.mat
    load D2.mat
    load T2.mat
else
    D1 = mvnrnd(mu1,cov1,200);
    save D1.mat D1
    T1 = mvnrnd(mu1,cov1,50);
    save T1.mat T1
    D2 = mvnrnd(mu2,cov2,200);
    save D2.mat D2
    T2 = mvnrnd(mu2,cov2,50);
    save T2.mat T2
end


plot_samples(D1,D2);
plot_pdf(mu1,cov1)
hold on 
plot_pdf(mu2,cov2)
hold off

% Maximum Likelihood Est. 

% train
mu1_estimate = MLE_mu(D1);
mu2_estimate = MLE_mu(D2);

% test
plot_samples(T1,T2);
plot_discriminant(mu1_estimate, cov1, mu2_estimate, cov2 );
title('Maximum Likelihood Estimation')
hold off

g1 = get_discriminant(mu1_estimate,cov1);
g2 = get_discriminant(mu2_estimate,cov2);

disp('Maximum Likelihood Est.')
test(g1,g2,T1,T2);



% Bayesian Parameter Est.

% train
epsilons = [-0.1 0.1 0.2];
n = length(D1);

disp('Bayesian Parameter Est.')
for i=1:length(epsilons)
    
    alfa_1 = 3 + epsilons(i);
    alfa_2 = -3 + epsilons(i);
    disp(['alfa1: ', num2str(alfa_1)]);
    disp(['alfa2: ', num2str(alfa_2)]);
    
    mu_bayes_1 = bayesian_guess(mu1_estimate, sigma, alfa_1, n );
    mu_bayes_2 = bayesian_guess(mu2_estimate, sigma, alfa_2, n );
    
    disp('mu_bayes_1: ')
    disp(mu_bayes_1);
      
    disp('mu_bayes_2: ')
    disp(mu_bayes_2);
   
    
    % test   
    plot_samples(T1,T2);
    plot_discriminant(mu_bayes_1, cov1, mu_bayes_2, cov2 );
    title(['Bayesian with alfa1: ', num2str(alfa_1), '  alfa2: ', num2str(alfa_2)]) 
    hold off
    
    
    g1 = get_discriminant(mu1_estimate,cov1);
    g2 = get_discriminant(mu2_estimate,cov2);
    test(g1,g2,T1,T2);

end





function plot_samples(x1,x2)
    figure
    plot(x1(:,1),x1(:,2),'or')
    hold on
    plot(x2(:,1),x2(:,2),'og')
    hold on
end


function plot_discriminant(m1,c1,m2,c2)
    [W1,w1,w01] = calculate_weights(m1,c1);
    [W2,w2,w02] = calculate_weights(m2,c2);
    
    syms x1 x2
    X=[x1;x2];
    range = 10;
    axis([-range,range,-range,range]); 
    g1 = transpose(X)*W1*X + transpose(w1)*X + w01;
    g2 = transpose(X)*W2*X + transpose(w2)*X + w02;
    
    disp('g1')
    disp(vpa(g1,3)) % Variable-precision arithmetic, simplify floats
    disp('g2')
    disp(vpa(g2,3))
    disp('g1-g2')
    disp( vpa((g1-g2), 3) )

    
    ezplot(g1-g2)
    hold on
end




function plot_pdf(mu,cov) 
    range = 10;
    x1 = -range:.1:range; 
    x2 = -range:.1:range; 
    mu = transpose(mu);
    [X, Y] = meshgrid(x1,x2); % all combinations of x, y
    Z = mvnpdf([X(:) Y(:)],mu,cov); % compute Gaussian pdf
    Z = reshape(Z,size(X)); % put into same size as X, Y
    contour(X,Y,Z); axis equal;  % contour plot; set same scale for x and y...
    % surf(X,Y,Z) % or 3D plot
end


function estimated_parameter = bayesian_guess(mu_estimate,sigma, alfa, n)
    % sigma0 = 1;
    sigma_square_n = ((sigma^2))/(n+(sigma^2));
    estimated_parameter = (n*mu_estimate/(n+sigma^2)) + sigma_square_n*alfa;
end


function mu_guess = MLE_mu(data)
    % Max Likelihood Est. 
    mu_guess = [0 0];
    n = length(data);
    
    for i=1:n
       sample = data(i,:);
       mu_guess = mu_guess + sample;
    end
    
    mu_guess = mu_guess ./ n;
    mu_guess = transpose(mu_guess);
end


function [W,w,w0] = calculate_weights(mu, cov)
    W = -0.5.*pinv(cov);
    w = pinv(cov)*mu;
    w0 = -0.5 .* (transpose(mu) * pinv(cov) * mu) - 0.5*log(det(cov)); 
end


function g = get_discriminant(mu, cov)
    W = -0.5.*pinv(cov);
    w = pinv(cov)*mu;
    w0 = -0.5 .* (transpose(mu) * pinv(cov) * mu) - 0.5*log(det(cov)); 
    g = @(x) transpose(x)*W*x + transpose(w)*x + w0;
end

function test(g1,g2,w1,w2)
    tp1 = 0;
    tn1 = 0;
    fp1 = 0;
    fn1 = 0;
    
    tp2 = 0;
    tn2 = 0;
    fp2 = 0;
    fn2 = 0;

            
    for i=1:length(w1)
        sample = w1(i,:);
        sample = transpose(sample);
        prediction = g1(sample)-g2(sample);
        if prediction > 0    
            tp1 = tp1+1;
            tn2 = tn2+ 1;
        else
            fn1 = fn1 + 1;
            fp2 = fp2 + 1;
        end
        
    end
    
    for i=1:length(w2)
        sample = w2(i,:);
        sample = transpose(sample);
        prediction = g1(sample)-g2(sample);
        if prediction < 0 
            tp2 = tp2+ 1;
            tn1 = tn1+ 1;
        else
            fn2 = fn2 + 1;
            fp1 = fp1 + 1;
        end
        
    end
    
    disp('     tp    tn    fp    fn');
    disp([tp1, tn1, fp1, fn1]);
    disp([tp2, tn2, fp2, fn2]);
    
    disp('precision1')
    precision1 = tp1/(tp1+fp1);
    disp(precision1)
    
    disp('recall1')
    recall1 = tp1/(tp1+fn1);
    disp(recall1)
    
    disp('precision2')
    precision2 = tp2/(tp2+fp2);
    disp(precision2)
    
    disp('recall2')
    recall2 = tp2/(tp2+fn2);
    disp(recall2)

end

   


