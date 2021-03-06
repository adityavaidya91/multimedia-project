package com.mm576.proj;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ImageSub {
	
	int width, height;
	BufferedImage javaImg;
	BufferedImage[] videoImgs;
	Mat cvImg;
	List<Mat> cvChannels;
	String name;
	//File file;
	
	public ImageSub(File file, int width, int height) {
		this.width = width;
		this.height = height;
		name = file.getName();
		if(file.length() == width*height*3) {
			readImage(file);
		}
		else {
			readVideo(file);
		}
		cvChannels = new LinkedList<Mat>();
		cvImg = ImageSub.img2Mat(cvChannels, javaImg);
	}
	
	//This is the starter code for the course
	public void readImage(File file){
		try{
			BufferedImage img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
			//File file = new File(fileName);
			InputStream is = new FileInputStream(file);
            long len = file.length();
            byte[] bytes = new byte[(int)len];

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
            int ind = 0;
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2]; 

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                    img.setRGB(x,y,pix);
                    ind++;
                }
            }
            //System.out.println(img);
			this.javaImg = img;
			is.close();
		}
		catch(Exception e) {
			System.out.println(e + "In file read, Images");
			this.javaImg = null;
		}
	}
	
	//Read video
	public void readVideo(File file) {
		try{
			InputStream is = new FileInputStream(file);

	        long len = file.length();
	        byte[] bytes = new byte[(int)len];
	        //BufferedImage img;

	        int numberOfFrames = 0;
	        int offset = 0; 
	        int numRead = 0;
	        int ind = 0;
	        while ( offset < bytes.length && (numRead=is.read(bytes, offset, 352*288*3)) >= 0) 
	        {
	            numberOfFrames++;
	            offset += numRead;
	        }	
	        this.videoImgs = new BufferedImage[numberOfFrames];
	        for(int i=0; i<numberOfFrames; i++){
	        	videoImgs[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        }
	        //System.out.println("Number of Frames in video = "+numberOfFrames);
	        for(int i=0; i<numberOfFrames; i++){
	            //img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	           // System.out.println("Processing frame number "+(i+1));
	            for(int y = 0; y < height; y++)
	            {
	                for(int x = 0; x < width; x++)
	                {
	                    byte r = bytes[ind];
	                    byte g = bytes[ind+height*width];
	                    byte b = bytes[ind+height*width*2]; 
	                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
	                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b)
	                    this.videoImgs[i].setRGB(x,y,pix);
	                    ind++;
	                }
	            }
	            ind+=height*width*2;
	            //imageCopy(img, this.videoImgs[i]);
	        }
	        this.javaImg = videoImgs[videoImgs.length/2];
	        is.close();
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
	public void imageCopy(BufferedImage input, BufferedImage output){ 
	  for (int i=0;i<output.getHeight();i++) {
	      for (int j=0;j<output.getWidth();j++) {
	          output.setRGB(j, i, input.getRGB(j, i));
	      }
	  }
	}
	
	public static void playVideo(ImageSub img) {
		long frameTime = (long) Math.pow(10, 3)/30;        
        JFrame frame = new JFrame(img.name);
        JLabel label = null;
        // Use a label to display the image
        for(int i=0; i < 2; i++)
	    {   
        	if(label != null)
        		frame.getContentPane().remove(label);
	        label = new JLabel(new ImageIcon(img.videoImgs[i]));
	        frame.getContentPane().add(label, BorderLayout.CENTER);
	        frame.pack();
	        frame.setVisible(true);
		    try{ 
		    	Thread.sleep(frameTime);
		    } 
		    catch(InterruptedException ie) { }
	    }
	}
	
	//This displays only one image
	public static void showResult(ImageSub img) {
	    try {
	        JFrame frame = new JFrame();
	        frame.getContentPane().add(new JLabel(new ImageIcon(img.javaImg)));
	        frame.setTitle(img.name);
	        frame.pack();
	        frame.setVisible(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	//Helper method from codeproject.com, modified a little for converting BufferedImage to Mat
	public static Mat img2Mat(List<Mat> mv, BufferedImage in)
    {
          Mat out;
          byte[] data;

          out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
          data = new byte[in.getHeight() * in.getWidth() * (int)out.elemSize()];
          int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
          for(int i = 0; i < dataBuff.length; i++)
          {
              data[i*3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
              data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
              data[i*3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
          }

           out.put(0, 0, data);
           
           Core.split(out, mv);
           for(Mat m : mv) {
        	   m.convertTo(m, CvType.CV_32F);
           }
           return out;
     }
}
