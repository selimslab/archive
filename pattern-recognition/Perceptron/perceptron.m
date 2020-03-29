clear
clc
close all

w1 = [ [0.1 1.1 ];
[6.8 7.1 ]
[-3.5 -4.1]
[2.0 2.7 ]
[4.1 2.8]
[3.1 5.0]
[-0.8 -1.3 ]
[0.9 1.2 ]
[5.0 6.4 ]
[3.9 4.0 ] ];



w2 = [[7.1 4.2]
[-1.4 -4.3]
[4.5 0]
[6.3 1.6]
[4.2 1.9]
[1.4 -3.2]
[2.4 -4.0]
[2.5 -6.1]
[8.4 3.7]
[4.1 -2.2]];

draw(w1,w2)


w1 = augment(w1);
w2 = augment(w2);

n = length(w1);
tr1 = w1(1:n/2, :);
test1 = w1(n/2+1:n, :);

tr2 = -1* w2(1:n/2, :);
test2 = -1* w2(n/2+1:n, :);

training = [tr1 ; tr2];


% Ho-Kashyap
[a,b] = ho(training);

disp(a)

plot_discriminant(a)

disp('Ho-Kashyap results')
test(tr1,tr2,a)

% Perceptron
a = ssp(training);

plot_discriminant(a)
legend({'w1', 'w2', 'Ho-Kashyap Hyperplane','Perceptron Hyperplane' },'Location','southeast')


disp('Perceptron results')
test(tr1,tr2,a)







function [a,b] = ho(Y)
    [m,d] = size(Y);
    a = ones(d,1);
    b = ones(m,1);
    n = 1;
    k_max = 100;
    k = 0;
    
    
    while k<k_max
        e = Y*a - b;
        b = b + n*(e+abs(e));
        a = pinv(Y)*b; 
        
        if abs(e)<0
            disp('not linearly separable')
        elseif abs(e) < 0.01
            disp('solved after')
            disp(k)
            break;    
        end
                
        if k >= k_max
            disp('max iterations reached')
        end
        k = k+1;
        
    end
  
end






function plot_discriminant(a)
    x1 = -5:10;
    x2 = -1*(a(2)*x1+a(1))/a(3);
    plot(x1,x2);
    hold on
end

function test(w1,w2,a)
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
        if dot(sample,a) >= 0 
            tp1 = tp1+ 1;
            tn2 = tn2+ 1;
        else
            fn1 = fn1 + 1;
            fp2 = fp2 + 1;
        end
        
    end
    
    for i=1:length(w2)
        sample = w2(i,:);
        if dot(sample,a) >= 0 
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

end

function aug=augment(x)
    n= length(x);
    aug = [ones(n,1) x];
end



function draw(w1,w2)
    figure
    plot(w1(:,1),w1(:,2), 'xr')
    hold on
    plot(w2(:,1),w2(:,2), 'xg')
    xlabel('x1')
    ylabel('x2')
    title('Perceptron and Ho-Kashyap')
    hold on
end


function a=ssp(samples)
    [n,d] = size(samples);
    a = zeros(d,1)';
    learning_rate = 1;
    correct = 0;
    while(correct < n)
        for i=1:n
            sample = samples(i,:);
            prediction = dot(a,sample);
            if prediction<=0
                a = a + learning_rate*sample;          
            else
                correct = correct + 1;
            end        
        end
    end
    

end



