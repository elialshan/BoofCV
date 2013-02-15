/*
 * Copyright (c) 2011-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.alg.feature.disparity.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;

import java.io.FileNotFoundException;

/**
 * @author Peter Abeles
 */
public class GenerateDisparityScoreSadRectFive extends CodeGeneratorBase {

	String typeInput;
	String dataAbr;
	String bitWise;
	String sumType;

	@Override
	public void generate() throws FileNotFoundException {
		createFile(AutoTypeImage.U8);
		createFile(AutoTypeImage.S16);
		createFile(AutoTypeImage.F32);
	}

	public void createFile( AutoTypeImage image ) throws FileNotFoundException {
		setOutputFile("ImplDisparityScoreSadRectFive_"+image.getAbbreviatedType());
		typeInput = image.getImageName();
		bitWise = image.getBitWise();
		sumType = image.getSumType();

		dataAbr = image.isInteger() ? "S32" : "F32";

		printPreamble();
		printProcess();
		printFirstRow();
		printComputeRemainingRows();
		printScoreFive();
		printTheRest();

		out.println("}");
	}

	private void printPreamble() {
		out.print("import boofcv.alg.InputSanityCheck;\n" +
				"import boofcv.alg.feature.disparity.DisparityScoreWindowFive;\n" +
				"import boofcv.alg.feature.disparity.DisparitySelect;\n" +
				"import boofcv.struct.image.ImageSingleBand;\n" +
				"import boofcv.struct.image."+typeInput+";\n" +
				"\n" +
				"/**\n" +
				" * <p>\n" +
				" * Implementation of {@link boofcv.alg.feature.disparity.DisparityScoreWindowFive} for processing\n" +
				" * images of type {@link "+typeInput+"}.\n" +
				" * </p>\n" +
				" *\n" +
				" * <p>\n" +
				" * DO NOT MODIFY. Generated by {@link GenerateDisparityScoreSadRectFive}.\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class "+className+"<Disparity extends ImageSingleBand>\n" +
				"\t\textends DisparityScoreWindowFive<"+typeInput+",Disparity>\n" +
				"{\n" +
				"\n" +
				"\t// Computes disparity from scores\n" +
				"\tDisparitySelect<"+sumType+"[],Disparity> computeDisparity;\n" +
				"\n" +
				"\t// stores the local scores for the width of the region\n" +
				"\t"+sumType+" elementScore[];\n" +
				"\t// scores along horizontal axis for current block\n" +
				"\t"+sumType+" horizontalScore[][];\n" +
				"\t// summed scores along vertical axis\n" +
				"\t// Save the last regionHeight scores in a rolling window\n" +
				"\t"+sumType+" verticalScore[][];\n" +
				"\t// In the rolling verticalScore window, which one is the active one\n" +
				"\tint activeVerticalScore;\n" +
				"\t// Where the final score it stored that has been computed from five regions\n" +
				"\t"+sumType+" fiveScore[];\n" +
				"\n" +
				"\tpublic "+className+"(int minDisparity, int maxDisparity,\n" +
				"\t\t\t\t\t\t\t\t\t\t\tint regionRadiusX, int regionRadiusY,\n" +
				"\t\t\t\t\t\t\t\t\t\t\tDisparitySelect<"+sumType+"[], Disparity> computeDisparity) {\n" +
				"\t\tsuper(minDisparity,maxDisparity,regionRadiusX,regionRadiusY);\n" +
				"\t\tthis.computeDisparity = computeDisparity;\n" +
				"\t}\n\n");
	}

	private void printProcess() {
		out.print("\t@Override\n" +
				"\tpublic void _process( "+typeInput+" left , "+typeInput+" right , Disparity disparity ) {\n" +
				"\t\tif( horizontalScore == null || verticalScore.length < lengthHorizontal ) {\n" +
				"\t\t\thorizontalScore = new "+sumType+"[regionHeight][lengthHorizontal];\n" +
				"\t\t\tverticalScore = new "+sumType+"[regionHeight][lengthHorizontal];\n" +
				"\t\t\telementScore = new "+sumType+"[ left.width ];\n" +
				"\t\t\tfiveScore = new "+sumType+"[ lengthHorizontal ];\n" +
				"\t\t}\n" +
				"\n" +
				"\t\tcomputeDisparity.configure(disparity,minDisparity,maxDisparity,radiusX*2);\n" +
				"\n" +
				"\t\t// initialize computation\n" +
				"\t\tcomputeFirstRow(left, right);\n" +
				"\t\t// efficiently compute rest of the rows using previous results to avoid repeat computations\n" +
				"\t\tcomputeRemainingRows(left, right);\n" +
				"\t}\n\n");
	}

	private void printFirstRow() {
		out.print("\t/**\n" +
				"\t * Initializes disparity calculation by finding the scores for the initial block of horizontal\n" +
				"\t * rows.\n" +
				"\t */\n" +
				"\tprivate void computeFirstRow( "+typeInput+" left, "+typeInput+" right ) {\n" +
				"\t\t"+sumType+" firstRow[] = verticalScore[0];\n" +
				"\t\tactiveVerticalScore = 1;\n" +
				"\n" +
				"\t\t// compute horizontal scores for first row block\n" +
				"\t\tfor( int row = 0; row < regionHeight; row++ ) {\n" +
				"\n" +
				"\t\t\t"+sumType+" scores[] = horizontalScore[row];\n" +
				"\n" +
				"\t\t\tUtilDisparityScore.computeScoreRow(left, right, row, scores,\n" +
				"\t\t\t\t\tminDisparity, maxDisparity, regionWidth, elementScore);\n" +
				"\t\t}\n" +
				"\n" +
				"\t\t// compute score for the top possible row\n" +
				"\t\tfor( int i = 0; i < lengthHorizontal; i++ ) {\n" +
				"\t\t\t"+sumType+" sum = 0;\n" +
				"\t\t\tfor( int row = 0; row < regionHeight; row++ ) {\n" +
				"\t\t\t\tsum += horizontalScore[row][i];\n" +
				"\t\t\t}\n" +
				"\t\t\tfirstRow[i] = sum;\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	private void printComputeRemainingRows() {
		out.print("\t/**\n" +
				"\t * Using previously computed results it efficiently finds the disparity in the remaining rows.\n" +
				"\t * When a new block is processes the last row/column is subtracted and the new row/column is\n" +
				"\t * added.\n" +
				"\t */\n" +
				"\tprivate void computeRemainingRows( "+typeInput+" left, "+typeInput+" right )\n" +
				"\t{\n" +
				"\t\tfor( int row = regionHeight; row < left.height; row++ , activeVerticalScore++) {\n" +
				"\t\t\tint oldRow = row%regionHeight;\n" +
				"\t\t\t"+sumType+" previous[] = verticalScore[ (activeVerticalScore -1) % regionHeight ];\n" +
				"\t\t\t"+sumType+" active[] = verticalScore[ activeVerticalScore % regionHeight ];\n" +
				"\n" +
				"\t\t\t// subtract first row from vertical score\n" +
				"\t\t\t"+sumType+" scores[] = horizontalScore[oldRow];\n" +
				"\t\t\tfor( int i = 0; i < lengthHorizontal; i++ ) {\n" +
				"\t\t\t\tactive[i] = previous[i] - scores[i];\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tUtilDisparityScore.computeScoreRow(left, right, row, scores,\n" +
				"\t\t\t\t\tminDisparity,maxDisparity,regionWidth,elementScore);\n" +
				"\n" +
				"\t\t\t// add the new score\n" +
				"\t\t\tfor( int i = 0; i < lengthHorizontal; i++ ) {\n" +
				"\t\t\t\tactive[i] += scores[i];\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tif( activeVerticalScore >= regionHeight-1 ) {\n" +
				"\t\t\t\t"+sumType+" top[] = verticalScore[ (activeVerticalScore -2*radiusY) % regionHeight ];\n" +
				"\t\t\t\t"+sumType+" middle[] = verticalScore[ (activeVerticalScore -radiusY) % regionHeight ];\n" +
				"\t\t\t\t"+sumType+" bottom[] = verticalScore[ activeVerticalScore % regionHeight ];\n" +
				"\n" +
				"\t\t\t\tcomputeScoreFive(top,middle,bottom,fiveScore,left.width);\n" +
				"\t\t\t\tcomputeDisparity.process(row - (1 + 4*radiusY) + 2*radiusY+1, fiveScore );\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	private void printScoreFive() {
		out.print("\t/**\n" +
				"\t * Compute the final score by sampling the 5 regions.  Four regions are sampled around the center\n" +
				"\t * region.  Out of those four only the two with the smallest score are used.\n" +
				"\t */\n" +
				"\tprotected void computeScoreFive( "+sumType+" top[] , "+sumType+" middle[] , "+sumType+" bottom[] , "+sumType+" score[] , int width ) {\n" +
				"\n" +
				"\t\t// disparity as the outer loop to maximize common elements in inner loops, reducing redundant calculations\n" +
				"\t\tfor( int d = minDisparity; d < maxDisparity; d++ ) {\n" +
				"\n" +
				"\t\t\t// take in account the different in image border between the sub-regions and the effective region\n" +
				"\t\t\tint indexSrc = (d-minDisparity)*width + (d-minDisparity) + radiusX;\n" +
				"\t\t\tint indexDst = (d-minDisparity)*width + (d-minDisparity);\n" +
				"\t\t\tint end = indexSrc + (width-d-4*radiusX);\n" +
				"\t\t\twhile( indexSrc < end ) {\n" +
				"\t\t\t\tint s = 0;\n" +
				"\n" +
				"\t\t\t\t// sample four outer regions at the corners around the center region\n" +
				"\t\t\t\t"+sumType+" val0 = top[indexSrc-radiusX];\n" +
				"\t\t\t\t"+sumType+" val1 = top[indexSrc+radiusX];\n" +
				"\t\t\t\t"+sumType+" val2 = bottom[indexSrc-radiusX];\n" +
				"\t\t\t\t"+sumType+" val3 = bottom[indexSrc+radiusX];\n" +
				"\n" +
				"\t\t\t\t// select the two best scores from outer for regions\n" +
				"\t\t\t\tif( val1 < val0 ) {\n" +
				"\t\t\t\t\t"+sumType+" temp = val0;\n" +
				"\t\t\t\t\tval0 = val1;\n" +
				"\t\t\t\t\tval1 = temp;\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tif( val3 < val2 ) {\n" +
				"\t\t\t\t\t"+sumType+" temp = val2;\n" +
				"\t\t\t\t\tval2 = val3;\n" +
				"\t\t\t\t\tval3 = temp;\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tif( val3 < val0 ) {\n" +
				"\t\t\t\t\ts += val2;\n" +
				"\t\t\t\t\ts += val3;\n" +
				"\t\t\t\t} else if( val2 < val1 ) {\n" +
				"\t\t\t\t\ts += val2;\n" +
				"\t\t\t\t\ts += val0;\n" +
				"\t\t\t\t} else {\n" +
				"\t\t\t\t\ts += val0;\n" +
				"\t\t\t\t\ts += val1;\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tscore[indexDst++] = s + middle[indexSrc++];\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public void printTheRest() {
		out.print("\t@Override\n" +
				"\tpublic Class<"+typeInput+"> getInputType() {\n" +
				"\t\treturn "+typeInput+".class;\n" +
				"\t}\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic Class<Disparity> getDisparityType() {\n" +
				"\t\treturn computeDisparity.getDisparityType();\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateDisparityScoreSadRectFive gen = new GenerateDisparityScoreSadRectFive();

		gen.generate();
	}
}