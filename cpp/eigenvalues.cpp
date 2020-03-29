// normalized power iteration and deflation to find eigen values of a vector.


#include < iostream > #include < fstream > #include < sstream > #include < math.h > #include < string > #include < stdlib.h > #include < cstring >

  using namespace std;

int i, j, k, n; // used in loops
float * * A, * * B, * * D, * x, * y, * e_vec_one, e_one, a, b, c, p; // used in matrix operations
fstream A_file; // to open-close .txt files
float max_val;

class matrix {
  // this is a class that holds all matrix operations together
  public:
    void mult_by_scalar(float * a, float b);
  void vector_mult(float * a, float * b, float * * c);

  void mat_substraction(float * * a, float * * b);
  void mat_scalar_substraction(float * * a, float b);

  void mat_multiplication(float * * a, float * b, float * c, float size); // matrix multiplication c=a*b

  void mat_division(float * c, float * a, float b, float size); //  division of matrices by a scalar  c=a/b

  void create_x(float d); //creates x matrix

  void calc_norm_inf(float * norm_mat); // finds matrix norm at infinity

  void normalized_power_iteration(float * * a, float tol, float d); // normalized power iteration algorithm
  float deflation(float e_one, float tol);

  int det_dim(); // determines dimensions of matrices 
  void allocate(); // allocates memory for matrices
  void read(); // reads the .txt file 

  void print_matrix(int rows, int cols, float * * mat); //prints n*n matrices
  void print_other(int rows, float * mat); // prints n*1 matrices

};

void matrix::mult_by_scalar(float * a, float b) {
  for (i = 0; i < n; i++) {
    a[i] = a[i] * b;
  }
}

void matrix::mat_multiplication(float * * a, float * b, float * c, float size) {
  // matrix multiplication c=a*b
  for (i = 0; i < size; i++) {
    //	for ( j=0; j<d; j++){
    c[i] = 0;
    for (k = 0; k < size; k++) {
      c[i] += (a[i][k] * b[k]);
    }
  }
  //}		
}

void matrix::vector_mult(float * a, float * b, float * * c) {
  for (i = 0; i < n; i++) {
    for (k = 0; k < n; k++) {
      c[i][k] = (a[i] * b[k]);
    }

  }
}

void matrix::mat_scalar_substraction(float * * a, float b) {
  for (i = 0; i < n; i++) {
    for (j = 0; j < n; j++) {
      a[i][j] -= b;

    }
  }
}

void matrix::mat_substraction(float * * a, float * * b) {
  for (i = 0; i < n; i++) {
    for (j = 0; j < n; j++) {
      b[i][j] = a[i][j] - b[i][j];
    }
  }
}

void matrix::mat_division(float * c, float * a, float b, float size) {
  //  division of matrices by a scalar  c=a/b
  for (i = 0; i < size; i++) {
    c[i] = a[i] / b;
  }

}

void matrix::create_x(float d) {
  //creates x matrix
  //	cout<<"starting vector is:"<<'\n';
  for (i = 0; i < d; i++)
    x[i] = 1;

}

void matrix::calc_norm_inf(float * norm_mat) {
  // finds matrix norm at infinity
  max_val = 0;
  for (j = 0; j < n; j++) {
    if ((fabs(norm_mat[j])) > max_val) {
      max_val = fabs(norm_mat[j]);
      e_one = norm_mat[j];

    }
  }

}

void matrix::normalized_power_iteration(float * * a, float tol, float d) {
  // normalized power iteration algorithm
  // for k=1,2..
  // y=Ax
  // |y|inf
  // x=y/|y|inf	end

  float y_norm = 0., dom_e_value;
  int iteration = 0;
  while (1) {
    iteration++;
    float old_norm = y_norm;

    mat_multiplication(a, x, y, d); // y=Ax

    //	print_other(n,y); cout<<'\n';

    calc_norm_inf(y); // |y|inf	
    y_norm = max_val;

    //	cout<<"y_norm is:"<< y_norm; cout<<'\n';

    mat_division(x, y, y_norm, n); // x=y/|y|inf

    //	cout<<"x is:"<<'\n';

    //	print_other(n,x); cout<<'\n';

    if (fabs(y_norm - old_norm) < tol) {
      break;
    }
  }

}

float matrix::deflation(float e_one, float tol) {

  //Wieland deflation algorithm is used
  // x= (1/e1*e_vector[1])*[first row of A]; e1 first eigen value
  //B=A-e1*e_vector*x(transpose)
  // B' ilk row ve columnlari at 
  // apply power iteration to B' and find second e-value

  float p = 1 / (e_one * x[1]); // 1/e1*e_vector[1];

  for (i = 0; i < n; i++) {
    e_vec_one[i] = x[i];
    x[i] = A[0][i]; // now x is the first row of A
  }

  mult_by_scalar(x, p); // x= (1/e1*e_vector[1])*[first row of A];

  vector_mult(e_vec_one, x, B);

  mat_substraction(A, B); // B= A-B

  //print_matrix(n,n,B);

  for (i = 0; i < n - 1; i++) {
    for (j = 0; j < n - 1; j++)
      D[i][j] = B[i + 1][j + 1]; // D is B', first row and column of B is truncated.
  }
  n--;

  normalized_power_iteration(D, tol, n);
  cout << "#E2: " << max_val << '\n';

}

int matrix::det_dim() {
  // determines dimensions of matrices 
  string line;
  n = 0;

  if (A_file.is_open()) {
    while (getline(A_file, line)) {
      ++n;
    }
    A_file.close();
  }
  return n;

}

void matrix::allocate() {
  // allocates memory for matrices
  A = new float * [n];
  B = new float * [n];
  D = new float * [n - 1];
  x = new float[n];
  y = new float[n];
  e_vec_one = new float[n];
  for (i = 0; i < n; i++) {
    A[i] = new float[n];
    B[i] = new float[n];
  }
  for (i = 0; i < n - 1; i++) {
    D[i] = new float[n - 1];
  }
}

void matrix::read() {
  // Reads input data
  for (i = 0; i < n; i++) {
    for (j = 0; j < n; j++) {
      A_file >> A[i][j];
    }
  }
  A_file.close();
}

void matrix::print_matrix(int rows, int cols, float * * mat) {
  //prints n*n matrices
  for (i = 0; i < rows; i++) {
    for (j = 0; j < cols; j++) {
      cout << mat[i][j] << " ";
    }
    printf("\n");
  }
}

void matrix::print_other(int rows, float * mat) {
  //prints n*1 matrices
  for (i = 0; i < rows; i++) {
    cout << mat[i];
    printf("\n");
  }

}

void print_out(char * a, string str) {
  // prints the solution on a text file 
  ofstream out_file;
  out_file.open(a);

  out_file << str << '\n';

  out_file.close();
}

void free_matrix(int rows, int cols, float * * mat) {

  for (i = 0; i < rows; i++) {
    for (j = 0; j < rows; j++) {
      free(mat[i]);
    }
  }
  free(mat);
}

int main(int argc, char * * argv) {

  double tolerance = 0.000001;
  string str;

  //reads file name of A matrice 

  cout << "enter file name of matrix A:" << '\n';
  cin >> str;
  char * a_mat = new char[str.length() + 1];
  strcpy(a_mat, str.c_str());

  // when it comes the ' ' character,it ends getting input
  // a_mat now contains a c-string copy of str

  //reads file name of out matrice, the same 

  cout << "enter file name of output matrix:" << '\n';
  cin >> str;
  char * out_mat = new char[str.length() + 1];
  strcpy(out_mat, str.c_str());

  //p = strtok (out_mat," ");

  A_file.open(a_mat);
  matrix newmat;
  // newmat is an object of class matrix, 
  // if we want to make simultaneous operations on various matrices,
  // we could define many matrix objects like it.
  newmat.det_dim(); // finds dimension of A matrix 
  A_file.open(a_mat);
  newmat.allocate(); // allocates memory for A
  newmat.read(); // reads the matrice
  //	newmat.print_matrix(n,n,A);
  newmat.create_x(n);
  //newmat.print_other(n,x);
  newmat.normalized_power_iteration(A, tolerance, n);
  cout << "#E1: " << e_one << '\n';
  cout << "e-vector: " << '\n';

  print_out(out_mat, "#E1: ");
  stringstream ss;
  ss << e_one;
  string out = ss.str();
  print_out(out_mat, out);

  for (i = 0; i < n; i++) {
    ss << x[i];
    string out = ss.str();
    print_out(out_mat, out);

  }

  newmat.print_other(n, x);
  cout << '\n';
  newmat.deflation(e_one, tolerance);

  print_out(out_mat, "#E2: ");
  ss << e_one;
  out = ss.str();
  print_out(out_mat, out);

}