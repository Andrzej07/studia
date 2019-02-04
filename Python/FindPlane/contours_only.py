from skimage import data, filters, morphology
from skimage.color import rgb2gray
from skimage.morphology import square
from matplotlib import pyplot as plt
import matplotlib.cm as cm
import numpy as np
import cv2


def main():
    plt.close('all')
    '''
    files = []
    for i in range(21):
        if(i != 6 and i != 15 and i != 2):
            nazwa = 'samolot'
            if(i<10): nazwa += '0'
            nazwa += str(i)
            nazwa += '.jpg'
            files.append(nazwa)
    '''
    files = ['samolot02.jpg','samolot07.jpg','samolot08.jpg','samolot09.jpg','samolot10.jpg','samolot11.jpg' ]
    background = [[0 for x in range(800*2)] for y in range(536*3)]                    
    plt.imshow(background, cmap=cm.Greys_r)
    #fig[0].tight_layout()
    #plt.subplots_adjust(wspace = 0.01, hspace=0.01)
    for ktory, nazwa in enumerate(files):
        plt.axis('off')
        image = data.imread(nazwa)
        copy = image
        #image = filters.rank.mean(image[:,:,0], np.ones([3,3], dtype=np.uint8))
        image = rgb2gray(image[:,:,2])
       # image = scipy.ndimage.gaussian_filter(image, sigma = 2)
        #image = morphology.erosion(image, square(2))
        
        image = filters.rank.median(image, np.ones([9,9], dtype = np.float64))
        image = filters.rank.mean(image, np.ones([2,2], dtype=np.float64))
        image = filters.sobel(image)
        image = image > 0.03
        image = morphology.dilation(image, square(4))
        image = morphology.erosion(image, square(4))
        #im = cv2.imread(nazwa)
        #ima = img_as_ubyte(image)
        #imgray = cv2.cvtColor(im,cv2.COLOR_BGR2GRAY)
        #ret, thresh = cv2.threshold(imgray,127,255,0)
        im = np.array(image, dtype = np.uint8)
        cpy, contours, hierarchy = cv2.findContours(im, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE) 
        #cv2.drawContours(image, contours, -1, (0,255,0), 3)
        copy = copy = np.array(copy, dtype = np.uint8)
       # thresh = filters.threshold_isodata(image)
       # image = image > thresh
       # image = feature.canny(image, sigma = 2)
        #image = scipy.ndimage.gaussian_filter(image**1.35, sigma = 0.5)
        #image = morphology.erosion(image, square(2)) 
        #image = denoise_bilateral(image, sigma_range=0.05, sigma_spatial=15)
       # image = filters.threshold_adaptive(image, 20, offset=10)
        maxlen = 0
        yoffset = 536 * (ktory%3)
        xoffset = 800 * (ktory%2)
        for contour in contours:
            if(len(contour)>maxlen): maxlen = len(contour)
        for component in zip(contours, hierarchy[0]):            
            contour = component[0]
            if(len(contour)> maxlen/4):
                currHierarchy = component[1]
                if(currHierarchy[3] == -1):
                    x_val = [x[0][0]+xoffset for x in contour]
                    y_val = [x[0][1]+yoffset for x in contour]
                    x_val.append(x_val[0]/2 + x_val[-1]/2)
                    y_val.append(y_val[0]/2 + y_val[-1]/2)
                    plt.plot(x_val, y_val, linewidth=0.65, color='w') 

            
    #plt.show()    
    plt.savefig("contourplanes3.png", format='png', dpi=900)  #bbox_inches='tight'

    
    
if __name__ == '__main__':
    main()