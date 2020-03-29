// finds the local minumum of a given polynomial by 3 different ways, 
// using secant and bisection methods and a hybrid of them 


#include < iostream > #include < iomanip > #include < cmath >

  using namespace std;

int i, n, count = 0, count_max = 100;
double * coeff, x0, x1, tol, x = 0;
double ilk, son;

class tools {

  public:

    double f(double x); // f function 

  void take_data(); // takes coefficients, guesses and tolerance

};

class methods: public tools {

  public:

    void hybrid(); // hybrid method

  int secant();

  int bisection();

};

double tools::f(double x) {

  double result;
  for (int i = 0; i < n + 1; i++) {

    result += coeff[i] * pow(x, n - i);
  }

  return result;
}

void tools::take_data() {

  cout << "degree of polynomial:";
  cin >> n;

  coeff = new double[n + 1];

  cout << "enter coefficients:\n";

  for (i = 0; i < n + 1; i++) {
    cin >> coeff[i];
  }

  cout << "enter x0, x1, tol: \n";
  cin >> x0;
  cin >> x1;
  cin >> tol;

  ilk = x0;
  son = x1;

}

void print() {

  cout << "the root is:" << x << "\n";
  cout << "number of iterations:" << count << "\n";

}

int methods::secant() {

  loop: x = (f(x1) * x0 - f(x0) * x1) / (f(x1) - f(x0)); // secant formula
  count++;

  if ((fabs((x - x1) / x)) >= tol) {
    // update values
    x0 = x1;
    x1 = x;

    if (count == count_max) {

      // to prevent ininite loop
      cout << "mission failed";
      goto stop;
    }

    goto loop;

  }

  stop: return 0;

}

void change_guess() {
  cout << "f(x0) anf f(x1) have same signs, enter new guesses:";
  cin >> x0;
  cin >> x1;
  ilk = x0;
  son = x1;

}

int methods::bisection() {

  if (f(x0) * f(x1) > 0) {
    //	f(x0) and f(x1) should have different signs by definition
    change_guess();
  }

  while (count < count_max) {

    if (count == count_max) {

      // to prevent ininite loop
      cout << "max iteration number reached";
      goto stop;
    }

    x = (x0 + x1) / 2; // find middle point

    count++;

    if (f(x) == 0 || x1 - x0 < tol) {
      // checks if x is a root, or tolerance reached
      goto stop;
    }

    if (f(x1) * f(x) > 0) {
      // sets x0=x if their f values have same sign  
      x0 = x;
    } else {
      x1 = x;
    }

  }
  stop:

    cout << "by bisection method:\n";
  print();

  return 0;

}

void methods::hybrid() {
  cout << "hybrid launched\n";

  methods in_hybrid;

  count = 0;
  count_max = 2; // just 2 iterations with bisection method

  in_hybrid.bisection();

  count_max = 100; // set it again

  in_hybrid.secant();

  cout << "by hybrid method:\n";
  print();

  count = 0;

}

void reset_data() {
  x0 = ilk;
  x1 = son;
}

int main() {

  methods blowakiss;
  tools fireagun; // arbitrary class object names

  fireagun.take_data(); //takes input data

  cout << "by secant method:\n";
  blowakiss.secant(); // secant method 
  print(); // print results

  reset_data(); // sets x0 and x1 to original values

  count = 0; // reset the counter

  blowakiss.bisection(); // bisection method

  reset_data();

  blowakiss.hybrid(); // hybrid method 

  return 0;

}