# -*- coding: utf-8 -*-
from __future__ import division
from numpy import *
from scipy import *
import wave
from scipy.signal import fftconvolve
import struct
import sys
import os
# Used for reading file
def everyOther (v, offset=0):
   return [v[i] for i in range(offset, len(v), 2)]

# Function for loading a wave file into numpy array
def readWave(filename):
    wav = wave.open (filename, "r")        
    (nchannels, sampwidth, w, nframes, comptype, compname) = wav.getparams ()
    frames = wav.readframes (nframes * nchannels)
    out = struct.unpack_from ("%dh" % nframes * nchannels, frames)
    # Convert 2 channels to numpy arrays
    if nchannels == 2:
        left = array (list (everyOther (out, 0)))
    else:
        left = array (out)
    
    data=left
    a = data.T[::] # this is a two channel soundtrack, I get the first track
    return w, a

def main():
	files = os.listdir(r'G:\KCKPython\RozpoznawaniePci\train')   
    
        correctGuesses = 0
        #os.chdir(r'G:\KCKPython\RozpoznawaniePci')
        #files = ['043_M.wav']
        for filename in files:
           	w, signal = readWave('train/' + filename)
           	if(len(signal)==0):
           	    print("Blad wczytywania")
           	    return
           	chunkSize = 5000
           	# Iterator used for dividing signal into chunks
           	current = 0
           	freqs = []
           	strs = []
           	# Divide the signal into chunks of chunkSize length,
           	# calculate base frequency and, if the frequency is appropriate for human voice,
           	# add it to freqs list. Finally compute average of freqs list.
           	while(current < len(signal)):
           	    if(len(signal)>current+chunkSize):
          		signal2 = signal[current:current+chunkSize]
           	    else:
          		signal2 = signal[current:len(signal)]
           	    pitch, strength = freq_from_autocorr(signal2, w)
           	    if(50 < pitch < 300):
          		freqs.append(pitch)
          		strs.append(strength) 
           	    current += chunkSize
           	if(len(freqs)!=0):
           	    finalFreq = 0
           	    strSum = sum(strs)
           	    for i, freq in enumerate(freqs):
          		finalFreq += freq * strs[i] / strSum
           	else:
           	    # Calculation failed, guess woman
           	    finalFreq = 1000
            
           	decision = 'K'
           	if(finalFreq < 165):
           	    decision = 'M'
           	print(decision)
           	if(filename[-5] == decision):
                    correctGuesses+=1
        print(str(correctGuesses) +" out of " + str(len(files)) + " correct!")

def freq_from_autocorr(sig, fs):
    """
    Estimate frequency using autocorrelation  
    If the signal is periodic, the autocorrelation function also will be, 
    and if the signal is harmonic the autocorrelation function will have
    peaks in multiples of the fundamental frequency. 
    """
    # Calculate autocorrelation (same thing as convolution, but with 
    # one input reversed in time), and throw away the negative lags
    corr = fftconvolve(sig, sig[::-1], mode='full')
    corr = corr[len(corr)/2:]
    
    # Find the first low point
    start = -1
    for i, x in enumerate(corr[:-1]):
	if( x < corr[i+1] ):
		start = i
		break
    if(start == -1):
	return 0, 0

    # Find the next peak after the low point (other than 0 lag).  This bit is 
    # not reliable for long signals, due to the desired peak occurring between 
    # samples, and other peaks appearing higher.
    # Should use a weighting function to de-emphasize the peaks at longer lags.
       
    peak = argmax(corr[start:]) + start
    px, py = parabolic(corr, peak)
    
    return fs / px, py
    

def parabolic(f, x):
    """Quadratic interpolation for estimating the true position of an
    inter-sample maximum when nearby samples are known.
   
    f is a vector and x is an index for that vector.
   
    Returns (vx, vy), the coordinates of the vertex of a parabola that goes
    through point x and its two neighbors.
   
    Example:
    Defining a vector f with a local maximum at index 3 (= 6), find local
    maximum if points 2, 3, and 4 actually defined a parabola.
   
    In [3]: f = [2, 3, 1, 6, 4, 2, 3, 1]
   
    In [4]: parabolic(f, argmax(f))
    Out[4]: (3.2142857142857144, 6.1607142857142856)
   
    """
    if(x+1 < len(f)):
        xv = 1/2. * (f[x-1] - f[x+1]) / (f[x-1] - 2 * f[x] + f[x+1]) + x
        yv = f[x] - 1/4. * (f[x-1] - f[x+1]) * (xv - x)
    else:
        xv = 1/2. * (f[x-1] - f[x]) / (f[x-1] - 2 * f[x] + f[x]) + x
        yv = f[x] - 1/4. * (f[x-1] - f[x]) * (xv - x)
    return (xv, yv)
        
if __name__ == '__main__':
    main()
