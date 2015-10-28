package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class BackPanel extends JPanel{
	public BackPanel(String filename) {
		URL url = getClass().getResource(filename);
		BufferedImage bi = getFilteredImage(url);
		CentredBackgroundBorder border = new CentredBackgroundBorder(bi);
		setBorder(border);
	}

	private BufferedImage getFilteredImage(URL url) {
		BufferedImage image;
		try {
			image = ImageIO.read(url);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		}
		BufferedImage dest = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_RGB);
		byte[] b = new byte[256];
		for (int i = 0; i < 256; i++)
			b[i] = (byte) (i);
		BufferedImageOp op = new LookupOp(new ByteLookupTable(0, b), null);
		op.filter(image, dest);
		return dest;
	}


}
