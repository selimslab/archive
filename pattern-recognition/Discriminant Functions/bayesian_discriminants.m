clear;
clc;
close all;

mu1 = [-1; -3];
mu2 = [3; 3];

% case 1 
sigma = 2;
cov1 = (sigma^2) .* [1 0; 0 1 ];
cov2 = (sigma^2) .* [1 0; 0 1 ];

disp('case 1')
solve_the_case(mu1,cov1,mu2,cov2)



% case 2 
sigma1 = 1;
sigma2 = 3;
cov1 = (sigma1^2) .* [1 0; 0 1 ];
cov2 = (sigma2^2) .* [1 0; 0 1 ];

disp('case 2')
solve_the_case(mu1,cov1,mu2,cov2)


% case 3 
cov1 = [1 0; 0 4 ];
cov2 = [4 0; 0 (1.5)^2 ];

disp('case 3')
solve_the_case(mu1,cov1,mu2,cov2)



function solve_the_case(mu1,cov1,mu2,cov2)
    % generate samples
    w1 = mvnrnd(mu1,cov1,50);
    w2 = mvnrnd(mu2,cov2,50);
    
    g1 = get_discriminant(mu1,cov1);
    g2 = get_discriminant(mu2,cov2);
    
    
    plot_samples(w1,w2);
    plot_pdf(mu1,cov1);
    hold on
    plot_pdf(mu2,cov2);
    hold on
    plot_discriminant(mu1,cov1,mu2,cov2);
    
    test(g1,g2,w1,w2);
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
    disp('g1')
    disp(vpa(g1,3)) % Variable-precision arithmetic, simplify floats
    g2 = transpose(X)*W2*X + transpose(w2)*X + w02;
    disp('g2')
    disp(vpa(g2,3))
    disp('g1-g2')
    disp( vpa((g1-g2), 3) )
    
    ezplot(g1-g2)
    title('')
    hold off
end


function plot_pdf(mu,cov) 
    mu = transpose(mu);
    range = 10;
    x1 = -range:.1:range; 
    x2 = -range:.1:range; 
    [X, Y] = meshgrid(x1,x2); % all combinations of x, y
    Z = mvnpdf([X(:) Y(:)],mu,cov); % compute Gaussian pdf
    Z = reshape(Z,size(X)); % put into same size as X, Y
    %surf(X,Y,Z) % or 3D plot

    contour(X,Y,Z); axis equal;  % contour plot; set same scale for x and y...
    title('Probability Density of the Dataset')
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
    true = 0;
    false = 0; 

    for i=1:length(w1)
        sample = w1(i,:);
        sample = transpose(sample);
        prediction = g1(sample)-g2(sample);
        if prediction > 0    
            true = true+1;
        else
            false = false+1;
        end
        
    end
    
    for i=1:length(w2)
        sample = w2(i,:);
        sample = transpose(sample);
        prediction = g1(sample)-g2(sample);
        if prediction < 0 
            true = true+1;
        else
            false = false+1;
        end
        
    end
    disp('correctly classified')
    disp(true)
    disp('misclassified')
    disp(false)

end