package boofcv.abst.tracker;

import boofcv.alg.tracker.circulant.CirculantTrackerOrig;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.ImageUInt8;
import org.junit.Test;

/**
 * @author Peter Abeles
 */
public class TestCirculantOrig_to_TrackerObjectQuad extends TextureGrayTrackerObjectRectangleTests {

	public TestCirculantOrig_to_TrackerObjectQuad() {
		tolStationary = 1;
	}

	@Override
	public TrackerObjectQuad<ImageUInt8> create(ImageType<ImageUInt8> imageType) {

		ConfigCirculantTracker config = new ConfigCirculantTracker();

		CirculantTrackerOrig alg = new CirculantTrackerOrig(
				config.output_sigma_factor,config.sigma,config.lambda,config.interp_factor,config.maxPixelValue);

		return new CirculantOrig_to_TrackerObjectQuad(alg, imageType);
	}

	@Test
	public void zooming_in() {
		// not supported
	}

	@Test
	public void zooming_out() {
		// not supported
	}
}
