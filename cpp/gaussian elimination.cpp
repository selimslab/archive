// Gaussian elimination algorithm 
// with partial pivoting and backward substitution 
// to solve Ax = b, 
// where A is an n*n square matrix. 


#include < iostream > #include < fstream > #include < string > #include < cmath > #include < vector >
  // I referred to the following resources 
  // https://martin-thoma.com/images/2013/05/Gaussian-elimination.png  for Gauss algorithm
  //https://github.com/AlbertoPa/machinePrecision/blob/master/machinePrecision.cpp  for machine precision calculations

  using namespace std;
int i, j, k, n;
float * * A;
float * b;
float * x;
fstream A_text;
fstream b_text;
float max_element;
int rownum;
float epsilon;

class matrix {
  // this class handles creating matrix by taking data from text file
  public:
    friend class gauss;
  int det_dim();
  void allocate();
  void read();
  void augment();
  void print();

};

class gauss {
  // this class keeps Gauss eliminatian related functions together
  public:
    int searchmax();
  void swap();
  void make_zero();
  void back_subs();
};

int gauss::searchmax() {

  // Search for maximum in columns
  for (k = i + 1; k < n; k++) {
    if (abs(A[k][i]) > max_element) {
      max_element = abs(A[k][i]);
      rownum = k;
    }
  }

}

void gauss::swap() {
  // Swaps maximum row with current row
  for (k = i; k < n + 1; k++) {
    float temp = A[rownum][k];
    A[rownum][k] = A[i][k];
    A[i][k] = temp;
  }

}

void gauss::make_zero() {
  // Makes the elements under pivots 0 
  for (k = i + 1; k < n; k++) {
    float temp = -A[k][i] / A[i][i];
    for (j = i; j < n + 1; j++) {
      if (i == j) {
        A[k][j] = 0;
      } else {
        A[k][j] += temp * A[i][j];
      }
    }
  }
}

void gauss::back_subs() {
  // back substitutes to solve the equation 

  for (i = n - 1; i >= 0; i--) {
    float * temp;
    temp = new float[n];
    int sum = 0;
    for (j = i + 1; j < n; j++) {
      sum += A[i][j] * temp[j];
      temp[i] = (A[i][n] - sum) / A[i][i];
    }
  }
}

void solve() {

  // Solve equation Ax=b for an upper triangular matrix A

  for (i = n - 1; i >= 0; i--) {

    x[i] = A[i][n] / A[i][i];

    for (k = i - 1; k >= 0; k--) {

      A[k][n] -= A[k][i] * x[i];
    }
  }

}

void printx() {
  // prints the solution on a text file 
  ofstream x_file;
  x_file.open("x.txt");
  for (i = 0; i < n; i++) {
    cout << x[i] << "\n";
    x_file << x[i] << "\n";
  }
  x_file.close();
}

// CLASS MATRIX PART

int matrix::det_dim() {

  string line;
  n = 0;

  if (b_text.is_open()) {
    while (getline(b_text, line)) {
      ++n;
    }
    b_text.close();
  }

  cout << "matrix A is " << n << "*" << n << "\n";
  return n;

}

void matrix::allocate() {
  A = new float * [n];
  b = new float[n];
  x = new float[n];
  for (k = 0; k < n; k++) {
    A[k] = new float[n];
  }
}

void matrix::read() {
  // Reads input data
  for (i = 0; i < n; i++) {
    for (j = 0; j < n; j++) {
      A_text >> A[i][j];
    }
  }
}

void matrix::augment() {
  // augments A and b matrices
  //	b.open("b_text");
  A[n] = new float[n];
  for (i = 0; i < n; i++) {
    b_text >> A[i][n];
  }
}

void matrix::print() {
  // prints matrix to screen
  printf("\n");
  for (i = 0; i < n; i++) {
    for (j = 0; j < n + 1; j++) {
      cout << A[i][j] << " ";

    }
    cout << "\n";
  }
}

//Precision	
float machine_precision() {
    // epsilon is the smallest float the machine recognizes
    // it is found by constantly dividing 1 by 2  
    epsilon = (float)(1.0);

    do {
      epsilon /= (float)(2.0);
    } while (((float)(1.0) + epsilon / 2.0) != 1.0);
    cout << "machine epsilon:" << epsilon << "\n";
    return epsilon;
  }
  //Singularity check 
void if_singular() {
  //checks if A is singular
  float mult = 1;

  for (i = 0; i < n; i++) {
    mult *= A[i][i];
    if (abs(mult) <= epsilon) {
      cout << "Error: the matrix A is not invertible, it is singular!!!" << "\n";
      break;
    }
  }

}

// Condition number part

float calc_norm_one() {
  // norm one for matrix
  float sum = 0;
  for (i = 0; i < n; i++) {
    sum += abs(A[i][n - 1]);
  }

  float norm_one = sum;
  return norm_one;
}

float calc_norm_inf() {
  // norm infinity for matrix
  float sum = 0;
  for (j = 0; j < n; j++) {
    sum += abs(A[n - 1][j]);
  }
  float norm_inf = sum;
  return norm_inf;
}

void invert_it() {
  i = 0;
  j = 0;
  float temp;
  float det = A[i + 1][i + 1] * A[i][i] - A[i][j + 1] * A[i + 1][j];

  temp = A[i + 1][i + 1];
  A[i + 1][i + 1] = A[i][i];
  A[i][i] = temp;

  temp = A[i][j + 1];
  A[i][j + 1] = -A[i + 1][j];
  A[i + 1][j] = -temp;

  for (i = 0; i < n; i++) {
    for (j = 0; j < n; j++) {
      A[i][j] = (1 / det) * A[i][j];

    }
  }
}

int main() {

  // tried to open with cmmnd line, but it doesnt work
  //	string  A_name,  b_name;
  //  cout << "Enter the name of A file in the filename.txt format" <<"\n";
  //	cin>>  A_name;
  // 	cout << "Enter the name of b file in the filename.txt format" <<"\n";
  //	cin >> b_name;

  A_text.open("A.txt");
  b_text.open("b.txt");

  matrix gotham;
  gotham.det_dim(); // finds dimension of A matrix
  gotham.allocate(); // allocates memory for A

  b_text.open("b.txt");

  gotham.read(); // reads the matrices

  cout << "the augmented matrix A|b:";
  gotham.augment(); // augments A and b
  gotham.print();

  // gaussian elimination 
  gauss trinity;
  for (i = 0; i < n; i++) {

    rownum = i; // row number of maximum element
    max_element = abs(A[i][i]);
    trinity.searchmax(); // finds max element in a column 
    trinity.swap(); // get the row of the max element to the top 
    trinity.make_zero(); // Makes the elements under pivots 0
    // repeats the same for every column
  }
  printf("the augmented matrix after Gaussian elimination: ");
  gotham.print();

  machine_precision(); // find machine epsilon 
  if_singular(); // singularity check

  trinity.back_subs(); // back substitutes 
  solve(); // solve Ax=b
  cout << "the solution is:" << "\n";
  printx(); // prints the solution on a text file

  A_text.close();
  A_text.open("A.txt");
  gotham.read();

  if (n == 2) // we'll find condition number for 2 by 2 matrices
  {

    float norm_1 = calc_norm_one();
    float norm_inf = calc_norm_inf();
    invert_it();
    float inv_norm_1 = calc_norm_one();
    float inv_norm_inf = calc_norm_inf();

    float cond_one = norm_1 * inv_norm_1;
    float cond_inf = norm_inf * inv_norm_inf;

    cout << "condition 1: " << cond_one << "\n" << "condition infinity: " << cond_inf;
  }

  return 0;

}