/*
 * Copyright [2009] [Marcin Rzeźnicki]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package mr.go.sgfilter;

/**
 * Pads data to left and/or right, starting from the first (last) non-zero cell
 * and extending it to the beginning (end) of data. More specifically:
 *
 *
 *
 * Let l be the index of the first non-zero element in data (for left
 * padding),
 * let r be the index of the last non-zero element in data (for
 * right padding)
 *
 * then for every element e which index is i such that:
 *
 *
 * 0 <= i < l, e is replaced with element data[l]
 * (left padding)
 *
 * r < i < data.length, e is replaced with element
 * data[r] (right padding)
 *
 *
 * Example:
 *
 * Given data: [0,0,0,1,2,1,3,1,2,4,0] result of applying
 * ContinuousPadder is: [1,1,1,1,2,1,3,1,2,4,0] in case of
 * {@link #isPaddingLeft() left padding}; [0,0,0,1,2,1,3,1,2,4,4] in
 * case of {@link #isPaddingRight() right padding};
 *
 * 
 * @author Marcin Rzeźnicki
 * 
 */
public class ContinuousPadder implements Preprocessor {

	private boolean paddingLeft = true;

	private boolean paddingRight = true;

	/**
	 * Default construcot. Both left and right padding are turned on
	 */
	public ContinuousPadder() {

	}

	/**
	 * 
	 * @param paddingLeft
	 *            enables or disables left padding
	 * @param paddingRight
	 *            enables or disables right padding
	 */
	public ContinuousPadder(boolean paddingLeft, boolean paddingRight) {
		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
	}

	@Override
	public void apply(double[] data) {
		int n = data.length;
		if (paddingLeft) {
			int l = 0;
			// seek first non-zero cell
			for (int i = 0; i < n; i++) {
				if (data[i] != 0) {
					l = i;
					break;
				}
			}
			double y0 = data[l];
			for (int i = 0; i < l; i++) {
				data[i] = y0;
			}
		}
		if (paddingRight) {
			int r = 0;
			// seek last non-zero cell
			for (int i = n - 1; i >= 0; i--) {
				if (data[i] != 0) {
					r = i;
					break;
				}
			}
			double ynr = data[r];
			for (int i = r + 1; i < n; i++) {
				data[i] = ynr;
			}
		}
	}

	/**
	 * 
	 * @return {@code paddingLeft}
	 */
	public boolean isPaddingLeft() {
		return paddingLeft;
	}

	/**
	 * 
	 * @return {@code paddingRight}
	 */
	public boolean isPaddingRight() {
		return paddingRight;
	}

	/**
	 * 
	 * @param paddingLeft
	 *            enables or disables left padding
	 */
	public void setPaddingLeft(boolean paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	/**
	 * 
	 * @param paddingRight
	 *            enables or disables right padding
	 */
	public void setPaddingRight(boolean paddingRight) {
		this.paddingRight = paddingRight;
	}

}
