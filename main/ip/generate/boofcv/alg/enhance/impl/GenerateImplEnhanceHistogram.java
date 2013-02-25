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

package boofcv.alg.enhance.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;

import java.io.FileNotFoundException;

/**
 * @author Peter Abeles
 */
public class GenerateImplEnhanceHistogram extends CodeGeneratorBase {
	String className = "ImplEnhanceHistogram";

	public GenerateImplEnhanceHistogram() throws FileNotFoundException {
		setOutputFile(className);
	}

	@Override
	public void generate() throws FileNotFoundException {
		printPreamble();

		applyTransform_U(AutoTypeImage.U8);
		applyTransform_U(AutoTypeImage.U16);
		applyTransform_S(AutoTypeImage.S8);
		applyTransform_S(AutoTypeImage.S16);
		applyTransform_S(AutoTypeImage.S32);

		out.print("\n" +
				"}\n");
	}

	private void printPreamble() {
		out.print("import boofcv.struct.image.*;\n" +
				"\n" +
				"/**\n" +
				" * <p>\n" +
				" * Functions for enhancing images using the image histogram.\n" +
				" * </p>\n" +
				" *\n" +
				" * <p>\n" +
				" * NOTE: Do not modify.  Automatically generated by {@link GenerateImplEnhanceHistogram}.\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class "+className+" {\n\n");
	}

	private void applyTransform_U( AutoTypeImage image ) {
		String typecast = image.getTypeCastFromSum();
		String bitwise = image.getBitWise();

		out.print("\tpublic static void applyTransform( "+image.getImageName()+" input , int transform[] , "+image.getImageName()+" output ) {\n" +
				"\t\tfor( int i = 0; i < input.height; i++ ) {\n" +
				"\t\t\tint indexInput = input.startIndex + i*input.stride;\n" +
				"\t\t\tint indexOutput = output.startIndex + i*output.stride;\n" +
				"\n" +
				"\t\t\tfor( int j = 0; j < input.width; j++ ) {\n" +
				"\t\t\t\toutput.data[indexOutput++] = "+typecast+"transform[input.data[indexInput++] "+bitwise+"];\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	private void applyTransform_S( AutoTypeImage image ) {
		String typecast = image.getTypeCastFromSum();

		out.print("\tpublic static void applyTransform( "+image.getImageName()+" input , int transform[] , int minValue , "+image.getImageName()+" output ) {\n" +
				"\t\tfor( int i = 0; i < input.height; i++ ) {\n" +
				"\t\t\tint indexInput = input.startIndex + i*input.stride;\n" +
				"\t\t\tint indexOutput = output.startIndex + i*output.stride;\n" +
				"\n" +
				"\t\t\tfor( int j = 0; j < input.width; j++ ) {\n" +
				"\t\t\t\toutput.data[indexOutput++] = "+typecast+"(transform[input.data[indexInput++]] + minValue);\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateImplEnhanceHistogram app = new GenerateImplEnhanceHistogram();
		app.generate();
	}
}
