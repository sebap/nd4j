package org.nd4j.linalg.fft;

import org.nd4j.linalg.api.complex.IComplexNDArray;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;
import org.nd4j.linalg.util.ComplexNDArrayUtil;

/**
 * Default FFT instance that will work that is backend agnostic.
 * @author Adam Gibson
 */
public class DefaultFFTInstance extends BaseFFTInstance {

    /**
     * FFT along a particular dimension
     * @param transform the ndarray to transform
     * @param numElements the desired number of elements in each fft
     * @return the ffted output
     */
    @Override
    public IComplexNDArray fft(INDArray transform,int numElements,int dimension) {
        IComplexNDArray inputC = Nd4j.createComplex(transform);
        if(inputC.isVector())
            return  new VectorFFT(numElements).apply(inputC);
        else {
            return rawfft(inputC,numElements,dimension);
        }
    }

    /**
     * 1d discrete fourier transform, note that this will
     * throw an exception if the passed in input
     * isn't a vector.
     * See matlab's fft2 for more information
     * @param inputC the input to transform
     * @return the the discrete fourier transform of the passed in input
     */
    @Override
    public  IComplexNDArray fft(IComplexNDArray inputC,int numElements,int dimension) {
        if(inputC.isVector())
            return  new VectorFFT(numElements).apply(inputC);
        else {
            return rawfft(inputC,numElements,dimension);
        }
    }



    /**
     * IFFT along a particular dimension
     * @param transform the ndarray to transform
     * @param numElements the desired number of elements in each fft
     * @param dimension the dimension to do fft along
     * @return the iffted output
     */
    @Override
    public IComplexNDArray ifft(INDArray transform,int numElements,int dimension) {
        IComplexNDArray inputC = Nd4j.createComplex(transform);
        if(inputC.isVector())
            return  new VectorIFFT(numElements).apply(inputC);
        else {
            return rawifft(inputC, numElements, dimension);
        }
    }

    /**
     * 1d discrete fourier transform, note that this will
     * throw an exception if the passed in input
     * isn't a vector.
     * See matlab's fft2 for more information
     * @param inputC the input to transform
     * @return the the discrete fourier transform of the passed in input
     */
    @Override
    public  IComplexNDArray ifft(IComplexNDArray inputC,int numElements,int dimension) {
        if(inputC.isVector())
            return  new VectorIFFT(numElements).apply(inputC);
        else {
            return rawifft(inputC,numElements,dimension);
        }
    }
    /**
     * FFT along a particular dimension
     * @param transform the ndarray to transform
     * @param numElements the desired number of elements in each fft
     * @return the ffted output
     */
    @Override
    public IComplexNDArray ifft(INDArray transform,int numElements) {
        IComplexNDArray inputC = Nd4j.createComplex(transform);
        if(inputC.isVector())
            return  new VectorIFFT(numElements).apply(inputC);
        else {
            return rawifft(inputC,numElements,inputC.shape().length - 1);
        }
    }

    /**
     * 1d discrete fourier transform, note that this will
     * throw an exception if the passed in input
     * isn't a vector.
     * See matlab's fft2 for more information
     * @param inputC the input to transform
     * @return the the discrete fourier transform of the passed in input
     */
    @Override
    public  IComplexNDArray ifft(IComplexNDArray inputC) {
        if(inputC.isVector())
            return  new VectorIFFT(inputC.length()).apply(inputC);
        else {
            return rawifft(inputC, inputC.size(inputC.shape().length - 1), inputC.shape().length - 1);
        }
    }


    /**
     * Underlying fft algorithm
     * @param transform the ndarray to transform
     * @param n the desired number of elements
     * @param dimension the dimension to do fft along
     * @return the transformed ndarray
     */
    @Override
    public IComplexNDArray rawfft(IComplexNDArray transform,int n,int dimension) {
        IComplexNDArray result = transform.dup();

        if(transform.size(dimension) != n) {
            int[] shape = ArrayUtil.copy(result.shape());
            shape[dimension] = n;
            if(transform.size(dimension) > n) {
                result = ComplexNDArrayUtil.truncate(result, n, dimension);
            }
            else
                result = ComplexNDArrayUtil.padWithZeros(result,shape);

        }


        if(dimension != result.shape().length - 1)
            result = result.swapAxes(result.shape().length - 1,dimension);



        result.iterateOverAllRows(new FFTSliceOp(result.size(result.shape().length - 1)));

        if(dimension != result.shape().length - 1)
            result = result.swapAxes(result.shape().length - 1,dimension);

        return result;
    }




    //underlying fftn
    @Override
    public IComplexNDArray rawifft(IComplexNDArray transform,int n,int dimension) {
        IComplexNDArray result = transform.dup();

        if(transform.size(dimension) != n) {
            int[] shape = ArrayUtil.copy(result.shape());
            shape[dimension] = n;
            if(transform.size(dimension) > n) {
                result = ComplexNDArrayUtil.truncate(result,n,dimension);
            }
            else
                result = ComplexNDArrayUtil.padWithZeros(result,shape);

        }


        if(dimension != result.shape().length - 1)
            result = result.swapAxes(result.shape().length - 1,dimension);

        result.iterateOverAllRows(new IFFTSliceOp(result.size(result.shape().length - 1)));

        if(dimension != result.shape().length - 1)
            result = result.swapAxes(result.shape().length - 1,dimension);

        return result;
    }

}
