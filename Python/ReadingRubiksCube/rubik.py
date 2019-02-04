# -*- coding: utf-8 -*-
from matplotlib import pyplot as plt
import numpy as np
import cv2
from matplotlib import cm
import math
from shapely.geometry import LineString

def main():
    plt.close('all')
    
    for zxc in range(1,25):
        filename = 'images/r'+str(zxc)+'.jpg'
        im = cv2.imread(filename)
        #im = cv2.bilateralFilter(im,9,75,75)
        #im = cv2.fastNlMeansDenoisingColored(im,None,10,10,7,21)
        kernel = np.ones((5,5),np.float32)/25
        im = cv2.filter2D(im,-1,kernel)
        width = len(im[0])
        height = len(im)
        black_img = np.zeros((height, width), dtype=np.uint8)
        hsv_img = cv2.cvtColor(im, cv2.COLOR_BGR2HSV)
        
        frame_threshed = cv2.inRange(im, np.array([0,0,0]), np.array([70,70,70]))
        kernel = np.ones((4,4),np.uint8)
        frame_threshed = cv2.dilate(frame_threshed,kernel,iterations = 3)
        frame_threshed = cv2.erode(frame_threshed,kernel,iterations = 2)   
   
        lines = cv2.HoughLinesP(frame_threshed,2,np.pi/180,threshold=100,minLineLength=250,maxLineGap=30)
        #print len(lines) 

        for i,line in enumerate(lines):
            for x1,y1,x2,y2 in line:
                cv2.line(im,(x1,y1),(x2,y2),(0,255,0),2)
                #cv2.circle(im, (int(x1),int(y1)), 1, (255,0,0), 2) 
        for i,line in enumerate(lines):   
            for x1,y1,x2,y2 in line:
                for j,line2 in enumerate(lines):
                    if(i!=j):
                        for x3,y3,x4,y4 in line2:
                            rads = math.atan2(y1-y2,x1-x2)
                            deg = math.degrees(rads)
                            rads2 = math.atan2((y3-y4),(x3-x4))
                            deg2 = math.degrees(rads2)
                            if abs(deg-deg2)>35:
                                line1 = LineString([(x1,y1), (x2,y2)])
                                line2 = LineString([(x3,y3), (x4,y4)])
                                point1 = line1.intersection(line2)
                                if point1:
                                    point = point1.coords.xy
                                    cv2.circle(black_img, (int(point[0][0]),int(point[1][0])), 1, (255,255,255), 2)

        kernel = np.ones((3,3),np.uint8)
        black_img = cv2.erode(black_img,kernel,iterations=3)
        black_img = cv2.dilate(black_img,kernel,iterations = 6)   
       # black_img = cv2.Canny(black_img, 10,10)
       # ret, black_img = cv2.threshold(black_img, 1,255, cv2.THRESH_BINARY)
        
        anc, contours, hierarchy = cv2.findContours(black_img,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
        points = []
        for component in zip(contours, hierarchy[0]):
                currHier = component[1]
                if(currHier[3] == - 1):
                    cnt = component[0]
                   # area = cv2.contourArea(cnt)
                    #if(area<70 and currHier[3]!=-1): cnt = contours[currHier[3]]
                    x,y,w,h = cv2.boundingRect(cnt)
                    brecol = [255,255,255]
                    cv2.rectangle(black_img,(x,y),(x+w,y+h),brecol,2)
                    points.append([x+w/2, y+h/2,w*h])                
        points = sorted(points, key=lambda el: el[2], reverse=True)
        points = points[0:19]
       # print points
        for point in points:
            cv2.circle(black_img, (point[0],point[1]), 3, (255,255,255), 2)

        left_points = points[:]
        left_points.sort()
        left = [[],[],[]]
        right = [[],[],[]]
        for i in range(0,3):
            column = sorted(left_points[0:(3+i)], key=lambda el: el[1])
            #print column
            left[0].append(column[0+i])
            left[1].append(column[1+i])
            left[2].append(column[2+i])
            for j in range(0,3+i):
                left_points.pop(0)
            if(i==2):
                right[0].append(left[0][2])
                right[1].append(left[1][2])
                right[2].append(left[2][2])
                column = sorted(left_points[0:4], key=lambda el: el[1])
                right[0].append(column[1])
                right[1].append(column[2])
                right[2].append(column[3])
                column = sorted(left_points[4:], key=lambda el: el[1])
                right[0].append(column[0])
                right[1].append(column[1])
                right[2].append(column[2])            
        for i in range(0,3):
            for j in range(0,3):
                if j < 2:
                    points.remove(left[i][j])
                points.remove(right[i][j])
        top = [[],[],[]]
        points = sorted(points, key=lambda el: el[1])
        top[0].append(points[0])
        points.pop(0)
        p1, p2  = points[0:2]
        if(p1[0] < p2[0]):
            top[1].append(p1)
            top[0].append(p2)
        else:
            top[1].append(p2)
            top[0].append(p1)     
        points.pop(0)
        points.pop(0)
        top[1].append(points[0])
        top[2] = left[0]
        top[0].append(right[0][2])
        top[1].append(right[0][1])
        #print right
        finalLeft = [[],[],[],[]]
        finalRight = [[],[],[],[]]
        finalTop = [[],[],[],[]]
        
        #najwyzszy                        
        distX,distY = calculateDists(top[2][2],top[1][1],top[0][0])
        pposX = int(top[0][0][0]-distX)
        pposY = int(top[0][0][1]-distY)
        cv2.circle(black_img, (pposX,pposY), 1, (255,255,255), 4)
        finalTop[0].append([pposX,pposY]) 
        #lewy dolny
        distX,distY = calculateDists(left[0][2],left[1][1],left[2][0])
        pposX = int(left[2][0][0]-distX)
        pposY = int(left[2][0][1]-distY)
        cv2.circle(black_img, (pposX,pposY), 1, (255,255,255), 4)    
        finalLeft[3].append([pposX,pposY])
    
        finalRight[0] = right[0][:]
        finalRight[1] = right[1][:]
        finalRight[2] = right[2][:]
        
        for i in range(0,3):
            #u gory po lewej
            distX,distY = calculateDists(top[i][2],top[i][1],top[i][0])
            pposX = int(top[i][0][0]-distX)
            pposY = int(top[i][0][1]-distY)
            cv2.circle(black_img, (pposX,pposY), 1, (255,255,255), 4)
            finalTop[i+1].append([pposX,pposY])
            #u gory po prawej
            distX,distY = calculateDists(top[2][i],top[1][i],top[0][i])
            pposX = int(top[0][i][0]-distX)
            pposY = int(top[0][i][1]-distY)
            cv2.circle(black_img, (pposX,pposY), 1, (255,255,255), 4)
            finalTop[0].append([pposX,pposY])
            #prawa sciana z prawej
            distX,distY = calculateDists(right[i][0],right[i][1],right[i][2])
            pposX = int(right[i][2][0]-distX)
            pposY = int(right[i][2][1]-distY)
            cv2.circle(black_img, (pposX,pposY), 1, (255,255,255), 4)
            finalRight[i].append([pposX,pposY])
            #prawa sciana na dole
            distX,distY = calculateDists(right[0][i],right[1][i],right[2][i])
            pposX = int(right[2][i][0]-distX)
            pposY = int(right[2][i][1]-distY)
            cv2.circle(black_img, (pposX,pposY), 1, (255,255,255), 4) 
            finalRight[3].append([pposX,pposY])       
            #lewa sciana z lewej
            distX,distY = calculateDists(left[i][2],left[i][1],left[i][0])
            pposX = int(left[i][0][0]-distX)
            pposY = int(left[i][0][1]-distY)
            cv2.circle(black_img, (pposX,pposY), 1, (255,255,255), 4)
            finalLeft[i].append([pposX,pposY])
            #lewa sciana na dole
            distX,distY = calculateDists(left[0][i],left[1][i],left[2][i])
            pposX = int(left[2][i][0]-distX)
            pposY = int(left[2][i][1]-distY)
            finalLeft[3].append([pposX,pposY])
            cv2.circle(black_img, (pposX,pposY), 1, (255,255,255), 4)    
            
        #prawy dolny
        distX,distY = calculateDists(right[0][0],right[1][1],right[2][2])
        pposX = int(right[2][2][0]-distX)
        pposY = int(right[2][2][1]-distY)
        cv2.circle(black_img, (pposX,pposY), 1, (255,255,255), 4)   
        finalRight[3].append([pposX,pposY])
        
        for i in range(0,3):
            for j in range(0,3):
                finalTop[i+1].append(top[i][j])
                finalLeft[i].append(left[i][j])
        '''
        print finalTop
        print finalLeft
        print finalRight    
        ODCZYTYWANIE KOLOROW
        '''
        left = finalLeft
        right = finalRight
        top = finalTop
        
        tested = []
        
        for i in range(0,3):
            for j in range(0,3):
                avg_x = (top[i][j][0]+top[i][j+1][0]+top[i+1][j][0]+top[i+1][j+1][0])/float(4)
                avg_y = (top[i][j][1]+top[i][j+1][1]+top[i+1][j][1]+top[i+1][j+1][1])/float(4)
                #print returnColor(hsv_img[int(avg_y)][int(avg_x)])
                tested.append(returnColor(hsv_img[int(avg_y)][int(avg_x)]))
                
        for i in range(0,3):
            for j in range(0,3):
                avg_x = (left[i][j][0]+left[i][j+1][0]+left[i+1][j][0]+left[i+1][j+1][0])/float(4)
                avg_y = (left[i][j][1]+left[i][j+1][1]+left[i+1][j][1]+left[i+1][j+1][1])/float(4)
                #print returnColor(hsv_img[int(avg_y)][int(avg_x)])
                tested.append(returnColor(hsv_img[int(avg_y)][int(avg_x)]))
                
        for i in range(0,3):
            for j in range(0,3):
                avg_x = (right[i][j][0]+right[i][j+1][0]+right[i+1][j][0]+right[i+1][j+1][0])/float(4)
                avg_y = (right[i][j][1]+right[i][j+1][1]+right[i+1][j][1]+right[i+1][j+1][1])/float(4)
                #print returnColor(hsv_img[int(avg_y)][int(avg_x)])
                tested.append(returnColor(hsv_img[int(avg_y)][int(avg_x)]))
                                                                    
        #plt.imshow(black_img)
        #plt.imshow(frame_threshed)
        #plt.show()
                                
        valid = []
        counter = 0
        correct = 0
        with open('results/r'+str(zxc)+'.out') as f:
            for line in f:
                if not line.strip():
                    continue
                for el in line.strip().split(' '):
                    valid.append(el)
                for i, el in enumerate(valid):
                    if(tested[i] == el):
                        correct += 1
                    counter += 1
        rate = round(correct/float(counter)*100,2)
        if(rate < 99):
            print tested
            print valid
            plt.subplots(1,2)
            ax = plt.subplot(1,2,1)
            ax.axis('off')
            plt.imshow(im)
            ax = plt.subplot(1,2,2)
            ax.axis('off')
            plt.imshow(black_img)      
            plt.show()
        print str(zxc)+": "+str(rate)
 
      
def calculateDists(p1,p2,p3):
    distX = ((p1[0] - p2[0]) + (p2[0] - p3[0])) / float(2)
    distY = ((p1[1] - p2[1]) + (p2[1] - p3[1])) / float(2)
    return distX,distY
                
def returnColor(hsv):
    # yellow, orange, blue, green
    colors = []
    colors.append([[26,120,50],[45,255,255],'Y']) # yellow
    colors.append([[10,120,50],[25,255,255], 'O']) # orange
    colors.append([[90,120,50],[150,255,255], 'B']) # blue
    colors.append([[45,110,50],[75,255,255], 'G']) # green
    colors.append([[0,110,50],[10,255,255], 'R']) # red
    colors.append([[165,110,50],[180,255,255], 'R']) # red again
    #colors.append([[0,1,100],[10,15,252], 'W']) # white
    for color in colors:
        if(color[0][0] <= hsv[0] <= color[1][0] and color[0][1] <= hsv[1] <= color[1][1] and color[0][2] <= hsv[2] <= color[1][2]):
            return color[2]
    #print hsv       
    return 'W'

if __name__ == '__main__':
    main()