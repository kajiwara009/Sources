package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;

class CentredBackgroundBorder implements Border {
	private final Insets insets = new Insets(0, 0, 0, 0);
	private final BufferedImage image;

	public CentredBackgroundBorder(BufferedImage image) {
		this.image = image;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		x += (width - image.getWidth()) / 2;
		y += (height - image.getHeight()) / 2;
		((Graphics2D) g).drawRenderedImage(image,
				AffineTransform.getTranslateInstance(x, y));
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}
}
