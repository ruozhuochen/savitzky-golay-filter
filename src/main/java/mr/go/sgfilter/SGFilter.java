package mr.go.sgfilter;

import static java.lang.Math.pow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import org.apache.commons.math.linear.RealMatrixImpl
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealVector;

/**
 * Savitzky-Golay filter implementation. For more information see
 * http://www.nrbook.com/a/bookcpdf/c14-8.pdf. This implementation,
 * however, does not use FFT
 * Note: modified version by DBO for compatibility with apache.commons.math3
 *
 * @author Marcin Rzeźnicki
 *
 */
public class SGFilter {

    /**
     * Computes Savitzky-Golay coefficients for given parameters
     *
     * @param nl
     *            numer of past data points filter will use
     * @param nr
     *            number of future data points filter will use
     * @param degree
     *            order of smoothin polynomial
     * @return Savitzky-Golay coefficients
     * @throws IllegalArgumentException
     *             if {@code nl < 0} or {@code nr < 0} or {@code nl + nr <
     *             degree}
     */
    public static double[] computeSGCoefficients(int nl, int nr, int degree) {
        if (nl < 0 || nr < 0 || nl + nr < degree)
            throw new IllegalArgumentException("Bad arguments");
        //RealMatrixImpl matrix = new RealMatrixImpl(degree + 1, degree + 1);
        Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(degree + 1, degree + 1);
        double[][] a = matrix.getDataRef();
        double sum;
        for (int i = 0; i <= degree; i++) {
            for (int j = 0; j <= degree; j++) {
                sum = (i == 0 && j == 0) ? 1 : 0;
                for (int k = 1; k <= nr; k++)
                    sum += pow(k, i + j);
                for (int k = 1; k <= nl; k++)
                    sum += pow(-k, i + j);
                a[i][j] = sum;
            }
        }

        double[] b = new double[degree + 1];
        b[0] = 1;

        final RealVector constantVector = new ArrayRealVector(b, false);
        DecompositionSolver solver = new LUDecomposition(matrix).getSolver();

        b = solver.solve(constantVector).toArray();

        //b = matrix.solve(b);
        double[] coeffs = new double[nl + nr + 1];
        for (int n = -nl; n <= nr; n++) {
            sum = b[0];
            for (int m = 1; m <= degree; m++)
                sum += b[m] * pow(n, m);
            coeffs[n + nl] = sum;
        }
        return coeffs;
    }

    private static void convertDoubleArrayToFloat(double[] in, float[] out) {
        for (int i = 0; i < in.length; i++)
            out[i] = (float) in[i];
    }

    private static void convertFloatArrayToDouble(float[] in, double[] out) {
        for (int i = 0; i < in.length; i++)
            out[i] = in[i];
    }

    private final List<DataFilter> dataFilters = new ArrayList<DataFilter>();

    private int nl;

    private int nr;

    private final List<Preprocessor> preprocessors = new ArrayList<Preprocessor>();

    /**
     * Constructs Savitzky-Golay filter which uses specified numebr of
     * surrounding data points
     *
     * @param nl
     *            numer of past data points filter will use
     * @param nr
     *            numer of future data points filter will use
     * @throws IllegalArgumentException
     *             of {@code nl < 0} or {@code nr < 0}
     */
    public SGFilter(int nl, int nr) {
        if (nl < 0 || nr < 0)
            throw new IllegalArgumentException("Bad arguments");
        this.nl = nl;
        this.nr = nr;
    }

    /**
     * Appends data filter
     *
     * @param dataFilter
     *            dataFilter
     * @see DataFilter
     */
    public void appendDataFilter(DataFilter dataFilter) {
        dataFilters.add(dataFilter);
    }

    /**
     * Appends data preprocessor
     *
     * @param p
     *            preprocessor
     * @see Preprocessor
     */
    public void appendPreprocessor(Preprocessor p) {
        preprocessors.add(p);
    }

    /**
     *
     * @return number of past data points that this filter uses
     */
    public int getNl() {
        return nl;
    }

    /**
     *
     * @return number of future data points that this filter uses
     */
    public int getNr() {
        return nr;
    }

    /**
     * Inserts data filter
     *
     * @param dataFilter
     *            data filter
     * @param index
     *            where it should be placed in data filters queue
     * @see DataFilter
     */
    public void insertDataFilter(DataFilter dataFilter, int index) {
        dataFilters.add(index, dataFilter);
    }

    /**
     * Inserts preprocessor
     *
     * @param p
     *            preprocessor
     * @param index
     *            where it should be placed in preprocessors queue
     * @see Preprocessor
     */
    public void insertPreprocessor(Preprocessor p, int index) {
        preprocessors.add(index, p);
    }

    /**
     * Removes data filter
     *
     * @param dataFilter
     *            data filter to be removed
     * @return {@code true} if data filter existed and was removed, {@code
     *         false} otherwise
     */
    public boolean removeDataFilter(DataFilter dataFilter) {
        return dataFilters.remove(dataFilter);
    }

    /**
     * Removes data filter
     *
     * @param index
     *            which data filter to remove
     * @return removed data filter
     */
    public DataFilter removeDataFilter(int index) {
        return dataFilters.remove(index);
    }

    /**
     * Removes preprocessor
     *
     * @param index
     *            which preprocessor to remove
     * @return removed preprocessor
     */
    public Preprocessor removePreprocessor(int index) {
        return preprocessors.remove(index);
    }

    /**
     * Removes preprocessor
     *
     * @param p
     *            preprocessor to be removed
     * @return {@code true} if preprocessor existed and was removed, {@code
     *         false} otherwise
     */
    public boolean removePreprocessor(Preprocessor p) {
        return preprocessors.remove(p);
    }

    /**
     * Sets number of past data points for this filter
     *
     * @param nl
     *            number of past data points
     * @throws IllegalArgumentException
     *             if {@code nl < 0}
     */
    public void setNl(int nl) {
        if (nl < 0)
            throw new IllegalArgumentException("nl < 0");
        this.nl = nl;
    }

    /**
     * Sets number of future data points for this filter
     *
     * @param nr
     *            number of future data points
     * @throws IllegalArgumentException
     *             if {@code nr < 0}
     */
    public void setNr(int nr) {
        if (nr < 0)
            throw new IllegalArgumentException("nr < 0");
        this.nr = nr;
    }

    /**
     * Smooths data by using Savitzky-Golay filter. This method will use 0 for
     * any element beyond {@code data} which will be needed for computation (you
     * may want to use some {@link Preprocessor})
     *
     * @param data
     *            data for filter
     * @param coeffs
     *            filter coefficients
     * @return filtered data
     * @throws NullPointerException
     *             when any array passed as parameter is null
     */
    public double[] smooth(double[] data, double[] coeffs) {
        return smooth(data, 0, data.length, coeffs);
    }

    /**
     * Smooths data by using Savitzky-Golay filter. Smoothing uses {@code
     * leftPad} and/or {@code rightPad} if you want to augment data on
     * boundaries to achieve smoother results for your purpose. If you do not
     * need this feature you may pass empty arrays (filter will use 0s in this
     * place, so you may want to use appropriate preprocessor)
     *
     * @param data
     *            data for filter
     * @param leftPad
     *            left padding
     * @param rightPad
     *            right padding
     * @param coeffs
     *            filter coefficients
     * @return filtered data
     * @throws NullPointerException
     *             when any array passed as parameter is null
     */
    public double[] smooth(double[] data, double[] leftPad, double[] rightPad,
                           double[] coeffs) {
        return smooth(data, leftPad, rightPad, 0, new double[][] { coeffs });
    }

    /**
     * Smooths data by using Savitzky-Golay filter. Smoothing uses {@code
     * leftPad} and/or {@code rightPad} if you want to augment data on
     * boundaries to achieve smoother results for your purpose. If you do not
     * need this feature you may pass empty arrays (filter will use 0s in this
     * place, so you may want to use appropriate preprocessor). If you want to
     * use different (probably non-symmetrical) filter near both ends of
     * (padded) data, you will be using {@code bias} and {@code coeffs}. {@code
     * bias} essentially means
     * "how many points of pad should be left out when smoothing". Filters
     * taking this condition into consideration are passed in {@code coeffs}.
     * coeffs[0] is used for unbiased data (that is, for
     * data[bias]..data[data.length-bias-1]). Its length has to be
     * nr + nl + 1. Filters from range
     * coeffs[coeffs.length - 1] to
     * coeffs[coeffs.length - bias] are used for smoothing first
     * {@code bias} points (that is, from data[0] to
     * data[bias]) correspondingly. Filters from range
     * coeffs[1] to coeffs[bias] are used for smoothing last
     * {@code bias} points (that is, for
     * data[data.length-bias]..data[data.length-1]). For example, if
     * you use 5 past points and 5 future points for smoothing, but have only 3
     * meaningful padding points - you would use {@code bias} equal to 2 and
     * would pass in {@code coeffs} param filters taking 5-5 points (for regular
     * smoothing), 5-4, 5-3 (for rightmost range of data) and 3-5, 4-5 (for
     * leftmost range). If you do not wish to use pads completely for
     * symmetrical filter then you should pass bias = nl = nr
     *
     * @param data
     *            data for filter
     * @param leftPad
     *            left padding
     * @param rightPad
     *            right padding
     * @param bias
     *            how many points of pad should be left out when smoothing
     * @param coeffs
     *            array of filter coefficients
     * @return filtered data
     * @throws IllegalArgumentException
     *             when bias &lt; 0 or bias &gt; min(nr, nl)
     * @throws IndexOutOfBoundsException
     *             when {@code coeffs} has less than 2*bias + 1
     *             elements
     * @throws NullPointerException
     *             when any array passed as parameter is null
     */
    public double[] smooth(double[] data, double[] leftPad, double[] rightPad,
                           int bias, double[][] coeffs) {
        if (bias < 0 || bias > nr || bias > nl)
            throw new IllegalArgumentException(
                    "bias < 0 or bias > nr or bias > nl");
        for (DataFilter dataFilter : dataFilters) {
            data = dataFilter.filter(data);
        }
        int dataLength = data.length;
        if (dataLength == 0)
            return data;
        int n = dataLength + nl + nr;
        double[] dataCopy = new double[n];
        // copy left pad reversed
        int leftPadOffset = nl - leftPad.length;
        if (leftPadOffset >= 0)
            for (int i = 0; i < leftPad.length; i++) {
                dataCopy[leftPadOffset + i] = leftPad[i];
            }
        else
            for (int i = 0; i < nl; i++) {
                dataCopy[i] = leftPad[i - leftPadOffset];
            }
        // copy actual data
        for (int i = 0; i < dataLength; i++) {
            dataCopy[i + nl] = data[i];
        }
        // copy right pad
        int rightPadOffset = nr - rightPad.length;
        if (rightPadOffset >= 0)
            for (int i = 0; i < rightPad.length; i++) {
                dataCopy[i + dataLength + nl] = rightPad[i];
            }
        else
            for (int i = 0; i < nr; i++) {
                dataCopy[i + dataLength + nl] = rightPad[i];
            }
        for (Preprocessor p : preprocessors) {
            p.apply(dataCopy);
        }
        // convolution (with savitzky-golay coefficients)
        double[] sdata = new double[dataLength];
        double[] sg;
        for (int b = bias; b > 0; b--) {
            sg = coeffs[coeffs.length - b];
            int x = (nl + bias) - b;
            double sum = 0;
            for (int i = -nl + b; i <= nr; i++) {
                sum += dataCopy[x + i] * sg[nl - b + i];
            }
            sdata[x - nl] = sum;
        }
        sg = coeffs[0];
        for (int x = nl + bias; x < n - nr - bias; x++) {
            double sum = 0;
            for (int i = -nl; i <= nr; i++) {
                sum += dataCopy[x + i] * sg[nl + i];
            }
            sdata[x - nl] = sum;
        }
        for (int b = 1; b <= bias; b++) {
            sg = coeffs[b];
            int x = (n - nr - bias) + (b - 1);
            double sum = 0;
            for (int i = -nl; i <= nr - b; i++) {
                sum += dataCopy[x + i] * sg[nl + i];
            }
            sdata[x - nl] = sum;
        }
        return sdata;
    }

    /**
     * Runs filter on data from {@code from} (including) to {@code to}
     * (excluding). Data beyond range spanned by {@code from} and {@code to}
     * will be used for padding
     *
     * @param data
     *            data for filter
     * @param from
     *            inedx of the first element of data
     * @param to
     *            index of the first element omitted
     * @param coeffs
     *            filter coefficients
     * @return filtered data
     * @throws ArrayIndexOutOfBoundsException
     *             if to &gt; data.length
     * @throws IllegalArgumentException
     *             if from &lt; 0 or to &gt; data.length
     * @throws NullPointerException
     *             if {@code data} is null or {@code coeffs} is null
     */
    public double[] smooth(double[] data, int from, int to, double[] coeffs) {
        return smooth(data, from, to, 0, new double[][] { coeffs });
    }

    /**
     * Runs filter on data from {@code from} (including) to {@code to}
     * (excluding). Data beyond range spanned by {@code from} and {@code to}
     * will be used for padding. See
     * {@link #smooth(double[], double[], double[], int, double[][])} for usage
     * of {@code bias}
     *
     * @param data
     *            data for filter
     * @param from
     *            inedx of the first element of data
     * @param to
     *            index of the first element omitted
     * @param bias
     *            how many points of pad should be left out when smoothing
     * @param coeffs
     *            filter coefficients
     * @return filtered data
     * @throws ArrayIndexOutOfBoundsException
     *             if to &gt; data.length or when {@code coeffs} has less
     *             than 2*bias + 1 elements
     * @throws IllegalArgumentException
     *             if from &lt; 0 or to &gt; data.length or
     *             from &gt; to or when bias &lt; 0 or
     *             bias &gt; min(nr, nl)
     * @throws NullPointerException
     *             if {@code data} is null or {@code coeffs} is null
     */
    public double[] smooth(double[] data, int from, int to, int bias,
                           double[][] coeffs) {
        double[] leftPad = Arrays.copyOfRange(data, 0, from);
        double[] rightPad = Arrays.copyOfRange(data, to, data.length);
        double[] dataCopy = Arrays.copyOfRange(data, from, to);
        return smooth(dataCopy, leftPad, rightPad, bias, coeffs);
    }

    /**
     * See {@link #smooth(double[], double[])}. This method converts {@code
     * data} to double for computation and then converts it back to float
     *
     * @param data
     *            data for filter
     * @param coeffs
     *            filter coefficients
     * @return filtered data
     * @throws NullPointerException
     *             when any array passed as parameter is null
     */
    public float[] smooth(float[] data, double[] coeffs) {
        return smooth(data, 0, data.length, coeffs);
    }

    /**
     * See {@link #smooth(double[], double[], double[], double[])}. This method
     * converts {@code data} {@code leftPad} and {@code rightPad} to double for
     * computation and then converts back to float
     *
     * @param data
     *            data for filter
     * @param leftPad
     *            left padding
     * @param rightPad
     *            right padding
     * @param coeffs
     *            filter coefficients
     * @return filtered data
     * @throws NullPointerException
     *             when any array passed as parameter is null
     */
    public float[] smooth(float[] data, float[] leftPad, float[] rightPad,
                          double[] coeffs) {
        return smooth(data, leftPad, rightPad, 0, new double[][] { coeffs });
    }

    /**
     * See {@link #smooth(double[], double[], double[], int, double[][])}. This
     * method converts {@code data} {@code leftPad} and {@code rightPad} to
     * double for computation and then converts back to float
     *
     * @param data
     *            data for filter
     * @param leftPad
     *            left padding
     * @param rightPad
     *            right padding
     * @param bias
     *            how many points of pad should be left out when smoothing
     * @param coeffs
     *            array of filter coefficients
     * @return filtered data
     * @throws IllegalArgumentException
     *             when bias &lt; 0 or bias &gt; min(nr, nl)
     * @throws IndexOutOfBoundsException
     *             when {@code coeffs} has less than 2*bias + 1
     *             elements
     * @throws NullPointerException
     *             when any array passed as parameter is null
     */
    public float[] smooth(float[] data, float[] leftPad, float[] rightPad,
                          int bias, double[][] coeffs) {
        double[] dataAsDouble = new double[data.length];
        double[] leftPadAsDouble = new double[leftPad.length];
        double[] rightPadAsDouble = new double[rightPad.length];
        convertFloatArrayToDouble(data, dataAsDouble);
        convertFloatArrayToDouble(leftPad, leftPadAsDouble);
        convertFloatArrayToDouble(rightPad, rightPadAsDouble);
        double[] results = smooth(dataAsDouble, leftPadAsDouble,
                                  rightPadAsDouble, bias, coeffs);
        float[] resultsAsFloat = new float[results.length];
        convertDoubleArrayToFloat(results, resultsAsFloat);
        return resultsAsFloat;
    }

    /**
     * See {@link #smooth(double[], int, int, double[])}. This method converts
     * {@code data} to double for computation and then converts it back to float
     *
     * @param data
     *            data for filter
     * @param from
     *            inedx of the first element of data
     * @param to
     *            index of the first element omitted
     * @param coeffs
     *            filter coefficients
     * @return filtered data
     * @throws ArrayIndexOutOfBoundsException
     *             if to &gt; data.length
     * @throws IllegalArgumentException
     *             if from &lt; 0 or to &gt; data.length
     * @throws NullPointerException
     *             if {@code data} is null or {@code coeffs} is null
     */
    public float[] smooth(float[] data, int from, int to, double[] coeffs) {
        return smooth(data, from, to, 0, new double[][] { coeffs });
    }

    /**
     * See {@link #smooth(double[], int, int, int, double[][])}. This method
     * converts {@code data} to double for computation and then converts it back
     * to float
     *
     * @param data
     *            data for filter
     * @param from
     *            inedx of the first element of data
     * @param to
     *            index of the first element omitted
     * @param bias
     *            how many points of pad should be left out when smoothing
     * @param coeffs
     *            filter coefficients
     * @return filtered data
     * @throws ArrayIndexOutOfBoundsException
     *             if to &gt; data.length or when {@code coeffs} has less
     *             than 2*bias + 1 elements
     * @throws IllegalArgumentException
     *             if from &lt; 0 or to &gt; data.length or
     *             from &gt; to or when bias &lt; 0 or
     *             bias &gt; min(nr, nl)
     * @throws NullPointerException
     *             if {@code data} is null or {@code coeffs} is null
     */
    public float[] smooth(float[] data, int from, int to, int bias,
                          double[][] coeffs) {
        float[] leftPad = Arrays.copyOfRange(data, 0, from);
        float[] rightPad = Arrays.copyOfRange(data, to, data.length);
        float[] dataCopy = Arrays.copyOfRange(data, from, to);
        return smooth(dataCopy, leftPad, rightPad, bias, coeffs);
    }
}
