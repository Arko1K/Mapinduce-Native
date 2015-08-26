import java.util.ArrayList;

/**
 * Created by arko1k on 15/6/15.
 */

class Tri {
    public Integer[] a, b, c;

    public Tri (Integer[] a, Integer[] b, Integer[] c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}

class Edge {
    public Integer[] a, b;

    public Edge (Integer[] a, Integer[] b) {
        this.a = a;
        this.b = b;
    }
}

public class PolyMath {
    public static double determinant(double A[][],int N)
    {
        double det=0;
        if(N == 1)
        {
            det = A[0][0];
        }
        else if (N == 2)
        {
            det = A[0][0]*A[1][1] - A[1][0]*A[0][1];
        }
        else
        {
            det=0;
            for(int j1=0;j1<N;j1++)
            {
                double[][] m = new double[N-1][];
                for(int k=0;k<(N-1);k++)
                {
                    m[k] = new double[N-1];
                }
                for(int i=1;i<N;i++)
                {
                    int j2=0;
                    for(int j=0;j<N;j++)
                    {
                        if(j == j1)
                            continue;
                        m[i-1][j2] = A[i][j];
                        j2++;
                    }
                }
                det += Math.pow(-1.0,1.0+j1+1.0)* A[0][j1] * determinant(m,N-1);
            }
        }
        return det;
    }

    private static double absDist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public static boolean pointInTri(Integer[] point, Tri tri) {

        // TODO: Optimize:

        int x1 = tri.a[0], y1 = tri.a[1], x2 = tri.b[0], y2 = tri.b[1], x3 = tri.c[0], y3 = tri.c[1];
        double a = determinant(new double[][]{{x1, y1, 1}, {x2, y2, 1}, {x3, y3, 1}}, 3);
        double bX = -determinant(new double[][]{{x1 * x1 + y1 * y1, y1, 1}, {x2 * x2 + y2 * y2, y2, 1}, {x3 * x3 + y3 * y3, y3, 1}}, 3);
        double bY = determinant(new double[][]{{x1 * x1 + y1 * y1, x1, 1}, {x2 * x2 + y2 * y2, x2, 1}, {x3 * x3 + y3 * y3, x3, 1}}, 3);
        double c = -determinant(new double[][]{{x1 * x1 + y1 * y1, x1, y1}, {x2 * x2 + y2 * y2, x2, y2}, {x3 * x3 + y3 * y3, x3, y3}}, 3);
        double x0 = -bX / 2 * a;
        double y0 = -bY / 2 * a;
        double circumradius = Math.sqrt(bX * bX + bY * bY - 4 * a * c) / 2 * Math.abs(a);
        return absDist(x0, y0, point[0], point[1]) <= circumradius;
    }

    public static ArrayList<Integer[]> delaunay(ArrayList<Integer[]> points) {
        ArrayList<Tri> triangulation = new ArrayList<Tri>();
        int maxX = 0, maxY = 0, len = points.size();
        for(int i = 0; i < len; i++) {
            Integer[] point = points.get(i);
            if(point[0] > maxX)
                maxX = point[0];
            if(point[1] > maxY)
                maxY = point[1];
        }
        Tri superTri = new Tri(new Integer[]{0, 0}, new Integer[]{0, maxX}, new Integer[]{0, maxY});
        triangulation.add(superTri);
        for(int i = 0; i < len; i++) {
            Integer[] point = points.get(i);
            ArrayList<Tri> badTriangles = new ArrayList<Tri>();
            int trSize = triangulation.size();
            for(int j = 0; j < trSize; j++) {
                Tri tri = triangulation.get(j);
                if(pointInTri(point, tri))
                    badTriangles.add(tri);
            }
            ArrayList<Edge> polygon = new ArrayList<Edge>();
            int badTrSize = triangulation.size();
            for(int j = 0; j < badTrSize; j++) {
                Tri tri = badTriangles.get(j);
                if(pointInTri(point, tri))
                    badTriangles.add(tri);
            }
        }

        return new ArrayList<Integer[]>();
    }
}