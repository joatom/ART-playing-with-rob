package de.joatom.imageProcessor.image.filter;

import de.joatom.imageProcessor.image.factory.AbstractImage;

/**
 * @author Johannes Tomasoni
 *
 */
public class Smoother {

	public enum Type {
		GAUSS, LINEAR
	};

	static final int[] gauss3V = { 1, 2, 1 };
	static final int gauss3Norm = 16;
	static final int[] linear3V = { 1, 1, 1 };
	static final int linear3Norm = 9;

	public static int gauss3(int[] img) {
		int p = 0;

		for (int i = 0; i < img.length; i++) {
			p = p + img[i] * Smoother.gauss3V[(int) Math.floor(i / Smoother.gauss3V.length)]
					* Smoother.gauss3V[i % Smoother.gauss3V.length];
		}
		;

		return p / Smoother.gauss3Norm;
	}

	public static int linear3(int[] img) {
		int p = 0;

		for (int i = 0; i < img.length; i++) {
			p = p + img[i] * Smoother.linear3V[(int) Math.floor(i / Smoother.linear3V.length)]
					* Smoother.linear3V[i % Smoother.linear3V.length];
		}
		;

		return p / Smoother.linear3Norm;
	}

	public static int[] smooth(AbstractImage img, Smoother.Type type) {

		final int width = img.getImg().getWidth();
		final int height = img.getImg().getHeight();
		int[] pixIn = img.getPix();
		int[] pix = pixIn.clone();

		for (int i = 0; i < pixIn.length; i++) {
			int x = i % width;
			int y = (int) Math.floor(i / width);
			// Do the convolution
			if (x != 0 && y != 0 && x < width - 1 && y < height - 1) {
				int mx[] = { ((pixIn[(i - width) - 1] >> 0) & 0xff), ((pixIn[(i - width)] >> 0) & 0xff),
						((pixIn[(i - width) + 1] >> 0) & 0xff), ((pixIn[i - 1] >> 0) & 0xff), ((pixIn[i] >> 0) & 0xff),
						((pixIn[i + 1] >> 0) & 0xff), ((pixIn[(i + width) - 1] >> 0) & 0xff),
						((pixIn[(i + width)] >> 0) & 0xff), ((pixIn[(i + width) + 1] >> 0) & 0xff) };
				int gaussPoint = 0;
				if (type == Smoother.Type.GAUSS) {
					gaussPoint = Smoother.gauss3(mx);
				} else if (type == Smoother.Type.LINEAR) {
					gaussPoint = Smoother.linear3(mx);
				}
				;
				pix[y * width + x] = gaussPoint;
			}
			// Frame keep original
			else {
				pix[y * width + x] = ((pixIn[i] >> 0) & 0xff);
			}
		}

		return pix;
	}

}
