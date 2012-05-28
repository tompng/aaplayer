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
	static int S=16;
	static String intToFF(int x){
		if(x<0)x=0;if(x>0xff)x=0xff;
		String s="0123456789abcdef";
		return s.charAt(x/16)+""+s.charAt(x%16);
	}
	public static void main(String args[])throws Exception{
		String fontfile="/System/Library/Fonts/Monaco.dfont";
		if(args.length==1){fontfile=args[0];}
		else{System.err.println("usage: java CharInfo FontFile > outputFile");return;}
		
		BufferedImage img=new BufferedImage(CW,CH,BufferedImage.TYPE_INT_RGB);
		Graphics2D g=(Graphics2D)img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(Font.createFont(Font.TRUETYPE_FONT,new File(fontfile)).deriveFont(Font.PLAIN,2*S));
		FontMetrics metrics=g.getFontMetrics();
		int ascent=metrics.getAscent(),descent=metrics.getDescent();
		for(int i=0;i<chars.length;i++){
			g.setColor(Color.white);
			g.fillRect(0,0,CW,CH);
			g.setColor(Color.black);
			g.drawString(""+chars[i],0,(CH+ascent-descent)/2);
			double sum0=0,sum1=0;
			for(int x=0;x<CW;x++)for(int y=0;y<CH;y++){
				double e=Math.exp(8.0*(y-CH/2.+0.5)/S);
				int col=img.getRGB(x,y)&0xff;
				sum0+=col/(1+e);
				sum1+=col*e/(1+e);
			}
			double av0=(sum0+0xff*S*S-0xff*CH*CW/2.0)/S/S;
			double av1=(sum1+0xff*S*S-0xff*CH*CW/2.0)/S/S;
			System.out.println(chars[i]+" "+intToFF((int)av0)+" "+intToFF((int)av1));
		}
	}
}