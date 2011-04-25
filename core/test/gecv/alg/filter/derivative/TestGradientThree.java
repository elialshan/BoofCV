/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.filter.derivative;

import gecv.alg.filter.convolve.ConvolveImage;
import gecv.core.image.UtilImageFloat32;
import gecv.core.image.UtilImageInt8;
import gecv.struct.convolve.Kernel1D_F32;
import gecv.struct.convolve.Kernel1D_I32;
import gecv.struct.image.ImageFloat32;
import gecv.struct.image.ImageInt16;
import gecv.struct.image.ImageInt8;
import gecv.testing.GecvTesting;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Peter Abeles
 */
public class TestGradientThree {

	Random rand = new Random(234);

	int width = 5;
	int height = 7;

	@Test
	public void checkInputShape() {
		GecvTesting.checkImageDimensionValidation(new GradientThree(), 6);
	}

	/**
	 * Compare the results to individually computed derivative along x and y axis.
	 */
	@Test
	public void deriv_I8() {
		ImageInt8 img = new ImageInt8(width, height);
		UtilImageInt8.randomize(img, rand, 0, 10);

		ImageInt16 derivX = new ImageInt16(width, height);
		ImageInt16 derivY = new ImageInt16(width, height);

		GecvTesting.checkSubImage(this, "deriv_I8", true, img, derivX, derivY);
	}

	public void deriv_I8(ImageInt8 img, ImageInt16 derivX, ImageInt16 derivY) {
		ImageInt16 derivX_a = new ImageInt16(width, height);
		ImageInt16 derivY_a = new ImageInt16(width, height);

		GradientThree.derivX_I8(img, derivX_a);
		GradientThree.derivY_I8(img, derivY_a);
		GradientThree.deriv_I8(img, derivX, derivY);

		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {
				assertEquals(derivX.get(j, i), derivX_a.get(j, i), 1e-5);
				assertEquals(derivY.get(j, i), derivY_a.get(j, i), 1e-5);
			}
		}
	}

	/**
	 * Compare the results to individually computed derivative along x and y axis.
	 */
	@Test
	public void deriv_F32() {
		ImageFloat32 img = new ImageFloat32(width, height);
		UtilImageFloat32.randomize(img, rand, 0f, 1.0f);

		ImageFloat32 derivX = new ImageFloat32(width, height);
		ImageFloat32 derivY = new ImageFloat32(width, height);

		GecvTesting.checkSubImage(this, "deriv_F32", true, img, derivX, derivY);
	}

	public void deriv_F32(ImageFloat32 img, ImageFloat32 derivX, ImageFloat32 derivY) {
		ImageFloat32 derivX_a = new ImageFloat32(width, height);
		ImageFloat32 derivY_a = new ImageFloat32(width, height);

		GradientThree.derivX_F32(img, derivX_a);
		GradientThree.derivY_F32(img, derivY_a);
		GradientThree.deriv_F32(img, derivX, derivY);

		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {
				assertEquals(derivX.get(j, i), derivX_a.get(j, i), 1e-5);
				assertEquals(derivY.get(j, i), derivY_a.get(j, i), 1e-5);
			}
		}
	}

	@Test
	public void derivX_I8() {
		ImageInt8 img = new ImageInt8(width, height);
		UtilImageInt8.randomize(img, rand, 0, 10);

		ImageInt16 derivX = new ImageInt16(width, height);
		ImageInt16 convX = new ImageInt16(width, height);

		GecvTesting.checkSubImage(this, "derivX_I8", true, img, derivX, convX);
	}

	public void derivX_I8(ImageInt8 img, ImageInt16 derivX, ImageInt16 convX) {
		GradientThree.derivX_I8(img, derivX);

		// compare to the equivalent convolution
		Kernel1D_I32 kernel = new Kernel1D_I32(3, -1, 0, 1);
		ConvolveImage.horizontal(kernel, img, convX, true);

		GecvTesting.assertEquals(derivX, convX, 1);
	}

	@Test
	public void derivY_I8() {
		ImageInt8 img = new ImageInt8(width, height);
		UtilImageInt8.randomize(img, rand, 0, 10);

		ImageInt16 derivY = new ImageInt16(width, height);
		ImageInt16 convY = new ImageInt16(width, height);

		GecvTesting.checkSubImage(this, "derivY_I8", true, img, derivY, convY);
	}

	public void derivY_I8(ImageInt8 img, ImageInt16 derivY, ImageInt16 convY) {
		GradientThree.derivY_I8(img, derivY);

		// compare to the equivalent convolution
		Kernel1D_I32 kernel = new Kernel1D_I32(3, -1, 0, 1);
		ConvolveImage.vertical(kernel, img, convY, true);

		GecvTesting.assertEquals(derivY, convY, 1);
	}

	@Test
	public void derivX_F32() {
		ImageFloat32 img = new ImageFloat32(width, height);
		UtilImageFloat32.randomize(img, rand, 0, 10);

		ImageFloat32 derivX = new ImageFloat32(width, height);
		ImageFloat32 convX = new ImageFloat32(width, height);

		GecvTesting.checkSubImage(this, "derivX_F32", true, img, derivX, convX);
	}

	public void derivX_F32(ImageFloat32 img, ImageFloat32 derivX, ImageFloat32 convX) {
		GradientThree.derivX_F32(img, derivX);

		// compare to the equivalent convolution
		Kernel1D_F32 kernel = new Kernel1D_F32(3, -0.5f, 0f, 0.5f);
		ConvolveImage.horizontal(kernel, img, convX, true);

		GecvTesting.assertEquals(derivX, convX, 0, 1);
	}

	@Test
	public void derivY_F32() {
		ImageFloat32 img = new ImageFloat32(width, height);
		UtilImageFloat32.randomize(img, rand, 0, 10);

		ImageFloat32 derivY = new ImageFloat32(width, height);
		ImageFloat32 convY = new ImageFloat32(width, height);

		GecvTesting.checkSubImage(this, "derivY_F32", true, img, derivY, convY);
	}

	public void derivY_F32(ImageFloat32 img, ImageFloat32 derivY, ImageFloat32 convY) {
		GradientThree.derivY_F32(img, derivY);

		// compare to the equivalent convolution
		Kernel1D_F32 kernel = new Kernel1D_F32(3, -0.5f, 0, 0.5f);
		ConvolveImage.vertical(kernel, img, convY, true);

		GecvTesting.assertEquals(derivY, convY, 0, 1);
	}
}