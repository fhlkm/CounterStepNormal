package com.example.tool;

public class BandPassFilter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] data ={3.12,3.13};
		// TODO Auto-generated method stub
		double[] kernel = bandPassKernel(10, 1.5d / (200/2), 7.5d / (200/2));
		double[] res = filter(data, kernel);

	}
	
	/**
     * See - http://www.mathworks.com/help/signal/ref/blackman.html
     * @param length
     * @return
     */
    public static double[] blackmanWindow(int length) {

        double[] window = new double[length];
        double factor = Math.PI / (length - 1);

        for (int i = 0; i < window.length; ++i) {
            window[i] = 0.42d - (0.5d * Math.cos(2 * factor * i)) + (0.08d * Math.cos(4 * factor * i));
        }

        return window;
    }

public static double[] lowPassKernel(int length, double cutoffFreq, double[] window) {

    double[] ker = new double[length + 1];
    double factor = Math.PI * cutoffFreq * 2; 
    double sum = 0;

    for (int i = 0; i < ker.length; i++) {
        double d = i - length/2; 
        if (d == 0) ker[i] = factor;
        else ker[i] =  Math.sin(factor * d) / d;
        ker[i] *= window[i];
        sum += ker[i];
    }

    // Normalize the kernel
    for (int i = 0; i < ker.length; ++i) {
        ker[i] /= sum;
    }

    return ker;
}

public static double[] bandPassKernel(int length, double lowFreq, double highFreq) {

    double[] ker = new double[length + 1];
    double[] window = blackmanWindow(length + 1);

    // Create a band reject filter kernel using a high pass and a low pass filter kernel 
    double[] lowPass = lowPassKernel(length, lowFreq, window);

    // Create a high pass kernel for the high frequency
    // by inverting a low pass kernel
    double[] highPass = lowPassKernel(length, highFreq, window);
    for (int i = 0; i < highPass.length; ++i) highPass[i] = -highPass[i];
    highPass[length / 2] += 1;

    // Combine the filters and invert to create a bandpass filter kernel
    for (int i = 0; i < ker.length; ++i) ker[i] = -(lowPass[i] + highPass[i]);
    ker[length / 2] += 1;

    return ker;
}

public static double[] filter(double[] signal, double[] kernel) {

    double[] res = new double[signal.length];

    for (int r = 0; r < res.length; ++r) {

        int M = Math.min(kernel.length, r + 1);
        for (int k = 0; k < M; ++k) {
            res[r] += kernel[k] * signal[r - k];
        }
    }

    return res;
}
public static float[][]filter2(float[][] signal,double[]kernel){
	double []accel_signal = new double[signal.length]; 
	for (int i =0;i<signal.length;i++){
		accel_signal[i] = signal[i][2];
	}
	double[]filter_accel_signal = filter(accel_signal,kernel);
	for(int j =0;j<signal.length;j++){
		signal[j][2] = (float)filter_accel_signal[j];
	}
	return signal;
	
}

}
