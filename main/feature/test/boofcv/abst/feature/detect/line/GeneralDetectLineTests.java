package boofcv.abst.feature.detect.line;


import boofcv.core.image.GeneralizedImageOps;
import boofcv.struct.image.ImageBase;
import boofcv.testing.BoofTesting;
import georegression.metric.UtilAngle;
import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.point.Point2D_F32;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Peter Abeles
 */
@SuppressWarnings({"unchecked"})
public abstract class GeneralDetectLineTests {

	int width = 40;
	int height = 50;

	double toleranceLocation = 1.5;
	double toleranceAngle = 0.1;

	int lineLocation = 10;

	Class[] imageTypes;

	protected GeneralDetectLineTests(Class... imageTypes) {
		this.imageTypes = imageTypes;
	}

	public abstract <T extends ImageBase> DetectLine<T> createAlg( Class<T> imageType );

	/**
	 * See if it can detect an obvious line in an image.
	 */
	@Test
	public void obviousLine() {
		for( Class c : imageTypes ) {
			obviousLine(c);
		}
	}

	/**
	 * Check to see if an subimage produces the same result as a regular image.
	 */
	@Test
	public void subImages() {
		for( Class c : imageTypes ) {
			subImages(c);
		}
	}

	private <T extends ImageBase> void obviousLine( Class<T> imageType ) {
		T input = GeneralizedImageOps.createImage(imageType,width,height);

		GeneralizedImageOps.fillRectangle(input,30,0,0,lineLocation,height);

		DetectLine<T> alg = createAlg(imageType);

		List<LineParametric2D_F32> found = alg.detect(input);

		assertTrue(found.size() >= 1);
		// see if at least one of the lines is within tolerance

		boolean foundMatch = false;
		for( LineParametric2D_F32 l : found ) {
			Point2D_F32 p = l.getPoint();
			double angle = l.getAngle();

			if( Math.abs(p.x-lineLocation) < toleranceLocation &&
				((UtilAngle.dist(Math.PI/2, angle) <= toleranceAngle) ||
					(UtilAngle.dist(-Math.PI/2, angle) <= toleranceAngle)) )
			{
				foundMatch = true;
				break;
			}
		}

		assertTrue(foundMatch);
	}

	private <T extends ImageBase> void subImages( Class<T> imageType ) {
		T input = GeneralizedImageOps.createImage(imageType,width,height);

		GeneralizedImageOps.fillRectangle(input,30,0,0,lineLocation,height);
		T sub = BoofTesting.createSubImageOf(input);

		List<LineParametric2D_F32> foundA = createAlg(imageType).detect(input);
	    List<LineParametric2D_F32> foundB = createAlg(imageType).detect(sub);

		// the output should be exactly identical
		assertEquals(foundA.size(),foundB.size());

		for( int i = 0; i < foundA.size(); i++ ) {
			LineParametric2D_F32 a = foundA.get(i);
			LineParametric2D_F32 b = foundB.get(i);

			assertTrue(a.slope.x == b.slope.x);
			assertTrue(a.slope.y == b.slope.y);
			assertTrue(a.p.x == b.p.x);
			assertTrue(a.p.y == b.p.y);
		}
	}
}
