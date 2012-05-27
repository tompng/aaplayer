import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.geom.*;
class CharInfo{
	static char chars[]={
32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126};
	static double[][]infos=new double[chars.length][2];
	static int CW=20,CH=40;
	static void makeCharImage(String fontfile)throws Exception{
		int up=CH,down=0;
		BufferedImage img=new BufferedImage(CW,CH,BufferedImage.TYPE_INT_RGB);
		Graphics2D g=(Graphics2D)img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(Font.createFont(Font.TRUETYPE_FONT,new File(fontfile)).deriveFont(Font.PLAIN,32));
		for(int i=0;i<chars.length;i++){
			g.setColor(Color.white);
			g.fillRect(0,0,CW,CH);
			g.setColor(Color.black);
			g.drawString(""+chars[i],(CW-CH/2)/2,CH*26/32);
			for(int x=0;x<CW;x++)for(int y=0;y<CH;y++){
				if((img.getRGB(x,y)&0xffffff)!=0xffffff){
					if(y<up)up=y;if(down<y)down=y;
				}
			}
		}
		System.out.println(up+" "+down+" "+(CH-(up+down+1))/2);
		for(int i=0;i<chars.length;i++){
			g.setColor(Color.white);
			g.fillRect(0,0,CW,CH);
			g.setColor(Color.black);
			g.drawString(""+chars[i],(CW-CH/2)/2,CH*26/32+(CH-(up+down+1))/2);
			for(int k=0;k<2;k++){
				int sum=0;
				for(int x=0;x<CW;x++)for(int y=0;y<CH/2;y++)sum+=img.getRGB(x,y+CH/2*k)&0xff;
				infos[i][k]=sum*2.0/CH/CW;
			}
			//ImageIO.write(img,"png",new File("chars/"+(int)chars[i]+".png"));
		}
		
	}
	static void makeIndex(String fname)throws Exception{
		BufferedImage img=new BufferedImage(256,256,BufferedImage.TYPE_INT_RGB);
		double difmap[][]=new double[256][256];
		for(int i=0;i<256;i++)for(int j=0;j<256;j++){
			double difmin=Double.MAX_VALUE;
			int im=0;
			for(int ii=0;ii<infos.length;ii++){
				double[]info=infos[ii];
				double dif=(i-info[0])*(i-info[0])+(j-info[1])*(j-info[1]);
				if(dif<difmin){difmin=dif;im=ii;}
			}
			difmap[i][j]=difmin;
			img.setRGB(i,j,(int)(0x7fffffff*Math.sin(im+0xffffff*Math.sin(im+1)+2)));
		}
		ImageIO.write(img,"png",new File("charindex_before.png"));
		double minmaxResult[][]=new double[256][256];
		double allrange=0;
		for(int i=0;i<256;i++)for(int j=0;j<256;j++)allrange+=difmap[i][j];
		
		for(int min=0;min<256;min++){
			double sumall=allrange;
			for(int max=255;max>=min;max--){
				minmaxResult[min][max]=sumall;
				sumall-=difmap[max][max];
				for(int i=min;i<max;i++)sumall-=difmap[max][i]+difmap[i][max];
			}
			allrange-=difmap[min][min];
			for(int i=min+1;i<256;i++)allrange-=difmap[min][i]+difmap[i][min];
		}
		int optMin=0,optMax=255;double optDif=Double.MAX_VALUE;
		for(int min=0;min<256-1;min++)for(int max=min+1;max<256;max++){
			if(max!=255)continue;//force using ' ' for color #ffffff
			double dif=Math.sqrt(minmaxResult[min][max])/(max-min+1)/(max-min+1);
			if(dif<optDif){optDif=dif;optMin=min;optMax=max;}
		}
		System.out.println(optMin+" "+optMax+" "+optDif);
		for(int i=0;i<infos.length;i++)for(int k=0;k<2;k++)infos[i][k]=255*(infos[i][k]-optMin)/(optMax-optMin);
		
		BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(new File(fname)));
		for(int i=0;i<256;i++)for(int j=0;j<256;j++){
			double difmin=Double.MAX_VALUE;
			int im=0;
			for(int ii=0;ii<infos.length;ii++){
				double[]info=infos[ii];
				double dif=(i-info[0])*(i-info[0])+(j-info[1])*(j-info[1]);
				if(dif<difmin){difmin=dif;im=ii;}
			}
			img.setRGB(i,j,(int)(0x7fffffff*Math.sin(im+0xffffff*Math.sin(im+1)+2)));
			out.write(chars[im]);
		}
		out.close();
		ImageIO.write(img,"png",new File("charindex_after.png"));
	}
	
	
	public static void main(String args[])throws Exception{
		String fontfile="/Library/Fonts/OsakaMono.ttf";
		String name="OsakaMono.chartable";
		if(args.length==2){fontfile=args[0];name=args[1];}
		else{System.out.println("usage: java CharInfo FontFile outputFile");return;}
		makeCharImage(fontfile);
		makeIndex(name);
	}
}