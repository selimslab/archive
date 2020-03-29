clear
clc
close all

mu = [4 2];
sigma = 3;
cov = (sigma^2)* eye(2);


% generate datasets
if isfile('Test100.mat')
    load R10.mat
    load R100.mat
    load R1000.mat
    load Test100.mat
    
else
    R10 = mvnrnd(mu,cov,10);
    save R10.mat R10
   
    R100 = mvnrnd(mu,cov,100);
    save R100.mat R100
    
    R1000 = mvnrnd(mu,cov,1000);
    save R1000.mat R1000
    
    Test100 = mvnrnd(mu,cov,100);
    save Test100.mat Test100
end


plot_pdf(mu,cov);


figure
x = R10;
plot(x(:,1),x(:,2), 'og')
title('10 samples')

figure
x = R100;
plot(x(:,1),x(:,2), '*r')
title('100 samples')


figure
x = R1000;
plot(x(:,1),x(:,2), '+b')
title('1000 samples')

figure
x = Test100;
plot(x(:,1),x(:,2), 'xg')
title('Test samples')



% Parzen

figure

subplot(3,3,1)
plot_parzen(R10,0.5)

subplot(3,3,2)
plot_parzen(R10,1.5)

subplot(3,3,3)
plot_parzen(R10,3)


subplot(3,3,4)
plot_parzen(R100,0.5)

subplot(3,3,5)
plot_parzen(R100,1.5)

subplot(3,3,6)
plot_parzen(R100,3)


subplot(3,3,7)
plot_parzen(R1000,0.5)

subplot(3,3,8)
plot_parzen(R1000,1.5)

subplot(3,3,9)
plot_parzen(R1000,3)

suptitle('Parzen density estimates')




% kNN

figure 
% 
R = R10;

subplot(3,3,1)
plot_knn_pdf_estimation(R,3)

subplot(3,3,2)
plot_knn_pdf_estimation(R,5)

subplot(3,3,3)
plot_knn_pdf_estimation(R,10)



R = R100;

subplot(3,3,4)
plot_knn_pdf_estimation(R,3)

subplot(3,3,5)
plot_knn_pdf_estimation(R,5)

subplot(3,3,6)
plot_knn_pdf_estimation(R,10)



R = R1000;
subplot(3,3,7)
plot_knn_pdf_estimation(R,3)

subplot(3,3,8)
plot_knn_pdf_estimation(R,5)

subplot(3,3,9)
plot_knn_pdf_estimation(R,10)

suptitle('kNN density estimates')


% test
h = 3;
k = 10;
knn_hit = 0;
parzen_hit = 0;


subplot(1,2,1)
plot_knn_pdf_estimation(R100,k)
subplot(1,2,2)
plot_parzen(R100,h)
suptitle('Classifiers')




for i=1:length(Test100)
    sample = Test100(i,:);
    
    actual_probability = mvnpdf(sample,mu,cov);
    parzen_estimate = parzen_density_estimation(R100,sample, h); 
    knn_estimate = knn_density_estimation(R100,sample,k);
    

    
    if abs(actual_probability-parzen_estimate) < abs(actual_probability-knn_estimate)
        knn_hit = knn_hit + 1;
    else
        parzen_hit = parzen_hit + 1;
    end
    
    %{
    disp(sample)
    disp(['actual probability:', num2str(actual_probability)])
    disp(['parzen estimate: ', num2str(parzen_estimate)])
    disp(['knn estimate: ', num2str(knn_estimate)])
    fprintf('\n')
    %}

end

    disp('Classification result')
    disp(['parzen closer: ', num2str(parzen_hit)])
    disp(['knn closer: ', num2str(knn_hit)])

    
    
% plotting methods

function plot_samples(x)
    plot(x(:,1),x(:,2), 'xb')
end

function plot_pdf(mu,cov) 
    figure
    range = 10;
    x1 = -range:.1:range; 
    x2 = -range:.1:range; 
    [X, Y] = meshgrid(x1,x2); % all combinations of x, y
    Z = mvnpdf([X(:) Y(:)],mu,cov); % compute Gaussian pdf
    Z = reshape(Z,size(X)); % put into same size as X, Y
    contour(X,Y,Z); axis equal;  % contour plot; set same scale for x and y...
    title('Probability Density of the Dataset')
    % surf(X,Y,Z) % or 3D plot
end


function plot_knn_pdf_estimation(samples, k) 
    range = 10;
    x1 = -range:.2:range; 
    x2 = -range:.2:range; 
    [X, Y] = meshgrid(x1,x2);
    centers = [X(:) Y(:)];
    n = length(centers);
    Z = zeros([n,1]);
    
    for i=1:n 
        x = centers(i,:);
        Z(i) = knn_density_estimation(samples,x, k);
    end
    
    Z = reshape(Z,size(X));
    contour(X,Y,Z); axis equal; 
    % surf(X,Y,Z);
    title(['k= ', num2str(k),  '  n= ', num2str(length(samples))])
end





% kNN methods

function estimated_probability = knn_density_estimation(samples, x, k)

    [n,d] = size(samples);
    r = max_radius_to_cover_knn(samples, x, k);  
    V = pi*(r^d);
    estimated_probability = (k/n)/V;
    
end


function r = max_radius_to_cover_knn(samples, center, k)

    n = length(samples);
    distances = zeros([n,1]);
    
    for i=1:n
        sample = samples(i,:);
        distances(i) = norm(center - sample);    
    end
    
    distances = sort(distances);
    k_nearest_distances = distances(k,:);
    r = max(k_nearest_distances);
    
end





% parzen methods

function plot_parzen(samples, h) 
    range = 10;
    x1 = -range:.3:range; 
    x2 = -range:.3:range; 
    [X, Y] = meshgrid(x1,x2); % all combinations of x, y
    Z = parzen_density_estimation(samples, [X(:) Y(:)], h ); 
    Z = reshape(Z,size(X)); % put into same size as X, Y
    % surf(X,Y,Z) % or 3D plot

    contour(X,Y,Z); axis equal;  % contour plot; set same scale for x and y...
    title(['h= ', num2str(h),  '  n= ', num2str(length(samples))])
end


function estimated_probability = parzen_density_estimation(samples, x, h)

    [n,d] = size(samples);
    k = 0;  
    % h=h/sqrt(n);
    
    for i=1:n
        xi = samples(i,:); 
        u = (1/h) .* (x-xi);    
        phi = mvnpdf(u);
        k = k + phi;    
    end
   
    V = h^d;
    estimated_probability = (k/n)/V;
   
end
