#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import division             # Division in Python 2.7
import matplotlib
matplotlib.use('Agg')                       # So that we can render files without GUI
import matplotlib.pyplot as plt
from matplotlib import rc
import numpy as np
import colorsys

def plot_color_map(sizeX, sizeY, heights, dist_between_pixels):
    # For pretty latex fonts (commented out, because it does not work on some machines)
    #rc('text', usetex=True) 
    #rc('font', family='serif', serif=['Times'], size=10)
    #rc('legend', fontsize=10)
    
    lowest = 110
    highest = 0
    for line in heights:       
        if(min(line) < lowest): lowest = min(line)
        if(max(line) > highest): highest = max(line)
    pt_per_inch = 72 
    plt.plot(figsize=(sizeY/ pt_per_inch, sizeY/ pt_per_inch))

    plt.xticks([0, 100, 200, 300, 400])
    plt.yticks([0, 100, 200, 300, 400])
    

    # Sun vector
    sun_vec = np.array([2,1.5,1])
    sun_vec = sun_vec / np.linalg.norm(sun_vec)

    # Create image with two lines and draw gradient on it
    min_angle = 10
    max_angle = -10
    almost_angles = []
    img = np.zeros((500, 500, 3))
    for i in range(1, sizeX-1):
        almost_angles_line = []
        for j in range(1, sizeY-1):
            arr = np.array([-dist_between_pixels*2, -(heights[i+1][j-1]-heights[i-1][j+1]), dist_between_pixels])
            arr2 = np.array([dist_between_pixels*2, -(heights[i+1][j+1]-heights[i-1][j-1]), dist_between_pixels])
            #wektor normalny
            surf_normal = np.cross(arr, arr2)
         #   print(surf_normal)
            surf_normal = surf_normal / np.linalg.norm(surf_normal)
         #   print(surf_normal)
            almost_angle = np.dot(sun_vec, surf_normal)
            almost_angles_line.append(almost_angle)
            if (almost_angle > max_angle):
                max_angle = almost_angle
            if (almost_angle < min_angle):
                min_angle = almost_angle   
        almost_angles.append(almost_angles_line) 
        #    print(almost_angle)
            #img[i][j] = gradient((heights[i][j]-lowest)/(highest-lowest), almost_angle)
        #    img[i][j] = gradient(almost_angle, 0)
    angle_diff = max_angle - min_angle
    print(min_angle)
    print(max_angle)
  #  print(almost_angles)
    for i in range(1, sizeX-1):
        for j in range(1, sizeY-1):
           #   if (i > 1 and j > 1):
           #      almost_angles[i-1][j-1] += almost_angles[i-1][j-2] + almost_angles[i-2][j-1] + almost_angles[i-2][j-2]
           #       almost_angles[i-1][j-1] /= 4        
              lighting = almost_angles[i-1][j-1]-min_angle
              lighting /= angle_diff
              img[i][j] = gradient((heights[i][j]-lowest)/(highest-lowest), lighting, 1-lighting)
     #dot product - kat miedzy wektorami
     #cross product - wektor normalny

    im = plt.imshow(img, aspect=1)
 #   im.set_extent([0, 1, 0, 1])

    
  #  x_text = 0.25
  #  y_text = 0.11
    # plt.text(x_text, y_text, '', va='center', ha='left', fontsize=10)

    plt.savefig('my-map.pdf')

def read_from_file(filename):
    heights = []
    with open(filename, 'r') as f:
        w, h, dist = [int(x) for x in f.readline().split()]
        for line in f:
            heights.append([float(x) for x in line.split()])
    return w, h, float(dist)/100, heights

def gradient(v, lighting, shading):
    s = 1; val = 1;
    h = 1/3 - v/3
    if (lighting > 0.8): print('light ' + str(lighting))
    if (shading > 0.8): print('shade ' + str(shading))
    
    #naswietlanie     
    if(lighting < 0.64): s = 1
    else: s = 1 - lighting*0.95
    #if (s < 0.08): s = 0.08
    
    # cieniowanie
    if(shading < 0.375): val = 1
    else:val = 1.375 - shading
    #if (val < 0): val = 0
    return colorsys.hsv_to_rgb(h, s, val)

if __name__ == '__main__':
    def toname(g):
        return g.__name__.replace('map_', '').replace('_', '-').upper()
    sizeX, sizeY, distance, heights = read_from_file('big.dem.txt')
    plot_color_map(sizeX, sizeY, heights, distance)

  #  gradients = (gradient_rgb_bw, gradient_rgb_gbr, gradient_rgb_gbr_full, gradient_rgb_wb_custom,
  #               gradient_hsv_bw, gradient_hsv_gbr, gradient_hsv_unknown, gradient_hsv_custom)

# plot_color_gradients(gradients, [toname(g) for g in gradients])
