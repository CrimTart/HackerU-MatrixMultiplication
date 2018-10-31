package com.company;

import java.util.Scanner;

//Square matrix multiplication using Strassen algorithm. Uses naive method for smaller matrices.

public class Main {
    private static Scanner scanner;

    public static void main(String[] args) {
        try {
            scanner = new Scanner(System.in);
            System.out.println("Input matrix size n:");
            int n = scanner.nextInt();
            if (n <= 0) throw new IllegalArgumentException("Matrix dimensions must be positive.");
            System.out.println("Input first matrix (by rows):");
            double[][] a = inputMatrix(n);
            System.out.println("Input second matrix (by rows):");
            double[][] b = inputMatrix(n);
            /*double[][] a = randomMatrix(n);
            System.out.println("First matrix:");
            printMatrix(a, n);
            double[][] b = randomMatrix(n);
            System.out.println("Second matrix:");
            printMatrix(b, n);*/
            int nn = newSize(n);
            double[][] c = multiplyStrassen(fillMatrixToSize(a, nn), fillMatrixToSize(b, nn), nn);
            System.out.println("Matrix multiplication:");
            printMatrix(c, n);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double[][] inputMatrix(int n) {
        double[][] res = new double[n][n];

        for (int i=0; i<n; i++)
            for(int j=0; j<n; j++)
                res[i][j] = scanner.nextDouble();
        return res;
    }

    private static void printMatrix(double[][] matrix, int n) {
        for (int i=0; i<n; i++ ) {
            for (int j=0; j<n; j++) {
                System.out.print(String.format("%.2f", matrix[i][j]) + " ");
            }
            System.out.println();
        }
    }

    private static double[][] randomMatrix(int n) {
        double[][] res = new double[n][n];
        for (int i=0; i<n; i++) {
            for (int j=0; j<n; j++) {
                res[i][j] = Math.random() * 100;
            }
        }
        return res;
    }

    private static double[][] multiplyNaive(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += a[i][k] * b[k][j];
                }
                c[i][j] = sum;
            }
        }
        return c;
    }

    //******************************************************************************************

    //Returns the smallest power of 2 greater than or equal to n.
    private static int newSize(int n) {
        int res = 1;
        while (res < n) res*=2;
        return res;
    }

    private static double[][] add(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                c[i][j] = a[i][j] + b[i][j];
            }
        }
        return c;
    }

    private static double[][] subtract(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                c[i][j] = a[i][j] - b[i][j];
            }
        }
        return c;
    }

    //Widens an existing matrix to a new one of desired size (new elements are zeroes).
    private static double[][] fillMatrixToSize(double[][] a, int n) {
        double[][] result = new double[n][n];

        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a[i], 0, result[i], 0, a[i].length);
        }
        return result;
    }

    //Splits a square matrix of side 2^k in four equal submatrices.
    private static void splitMatrix(double[][] a, double[][] a11, double[][] a12, double[][] a21, double[][] a22) {
        int n = a.length / 2;

        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, a11[i], 0, n);
            System.arraycopy(a[i], n, a12[i], 0, n);
            System.arraycopy(a[i + n], 0, a21[i], 0, n);
            System.arraycopy(a[i + n], n, a22[i], 0, n);
        }
    }

    //Glues a previously split matrix back together (see splitMatrix).
    private static double[][] glueMatrix(double[][] a11, double[][] a12, double[][] a21, double[][] a22) {
        int n = a11.length;
        double[][] a = new double[n*2][n*2];

        for (int i = 0; i < n; i++) {
            System.arraycopy(a11[i], 0, a[i], 0, n);
            System.arraycopy(a12[i], 0, a[i], n, n);
            System.arraycopy(a21[i], 0, a[i + n], 0, n);
            System.arraycopy(a22[i], 0, a[i + n], n, n);
        }
        return a;
    }

    //Main multiplication method. Cutoff for n taken arbitrarily, after reading various sources (no conclusive answer).
    private static double[][] multiplyStrassen(double[][] a, double[][] b, int n) {
        if (n <= 64) return multiplyNaive(a, b);

        n /= 2;
        double[][] a11 = new double[n][n], a12 = new double[n][n], a21 = new double[n][n], a22 = new double[n][n];
        double[][] b11 = new double[n][n], b12 = new double[n][n], b21 = new double[n][n], b22 = new double[n][n];

        splitMatrix(a, a11, a12, a21, a22);
        splitMatrix(b, b11, b12, b21, b22);

        double[][] p1 = multiplyStrassen(add(a11, a22), add(b11, b22), n);
        double[][] p2 = multiplyStrassen(add(a21, a22), b11, n);
        double[][] p3 = multiplyStrassen(a11, subtract(b12, b22), n);
        double[][] p4 = multiplyStrassen(a22, subtract(b21, b11), n);
        double[][] p5 = multiplyStrassen(add(a11, a12), b22, n);
        double[][] p6 = multiplyStrassen(subtract(a21, a11), add(b11, b12), n);
        double[][] p7 = multiplyStrassen(subtract(a12, a22), add(b21, b22), n);

        double[][] c11 = add(add(p1, p4), subtract(p7, p5));
        double[][] c12 = add(p3, p5);
        double[][] c21 = add(p2, p4);
        double[][] c22 = add(subtract(p1, p2), add(p3, p6));

        return glueMatrix(c11, c12, c21, c22);
    }
}
