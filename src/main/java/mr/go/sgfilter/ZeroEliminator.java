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
 * Eliminates zeros from data - starting from the first non-zero element, ending
 * at the last non-zero element. More specifically:
 *
 *
 *
 * Let l be the index of the first non-zero element in data,
 * let r be the index of the last non-zero element in data
 *
 * then for every element e which index is i such that:
 * l < i < r and e == 0, e is replaced with element e'
 * with index j such that:
 *
 * l <= j < i and e' <> 0 and for all indexes
 * k: j < k < i; e[k] == 0 - when {@link #isAlignToLeft() alignToLeft}
 * is true
 * i < j <= r and e' <> 0 and for all indexes
 * k: i < k < j;e[k] == 0 - otherwise
 *
 *
 * Example:
 *
 * Given data: [0,0,0,1,2,0,3,0,0,4,0] result of applying
 * ZeroEliminator is: [0,0,0,1,2,2,3,3,3,4,0] if
 * {@link #isAlignToLeft() alignToLeft} is true;
 * [0,0,0,1,2,3,3,4,4,4,0] - otherwise
 *
 * 
 * @author Marcin Rzeźnicki
 * 
 */
public class ZeroEliminator implements Preprocessor {

	private boolean alignToLeft;

	/**
	 * Default constructor: {@code alignToLeft} is {@code false}
	 * 
	 * @see #ZeroEliminator(boolean)
	 */
	public ZeroEliminator() {

	}

	/**
	 * 
	 * @param alignToLeft
	 *            if {@code true} zeros will be replaced with non-zero element
	 *            to the left, if {@code false} - to the right
	 */
	public ZeroEliminator(boolean alignToLeft) {
		this.alignToLeft = alignToLeft;
	}

	@Override
	public void apply(double[] data) {
		int n = data.length;
		int l = 0, r = 0;
		// seek first non-zero cell
		for (int i = 0; i < n; i++) {
			if (data[i] != 0) {
				l = i;
				break;
			}
		}
		// seek last non-zero cell
		for (int i = n - 1; i >= 0; i--) {
			if (data[i] != 0) {
				r = i;
				break;
			}
		}
		// eliminate 0s
		if (alignToLeft)
			for (int i = l + 1; i < r; i++) {
				if (data[i] == 0) {
					data[i] = data[i - 1];
				}
			}
		else
			for (int i = r - 1; i > l; i--) {
				if (data[i] == 0) {
					data[i] = data[i + 1];
				}
			}
	}

	/**
	 * 
	 * @return {@code alignToLeft}
	 */
	public boolean isAlignToLeft() {
		return alignToLeft;
	}

	/**
	 * 
	 * @param alignToLeft
	 *            if {@code true} zeros will be replaced with non-zero element
	 *            to the left, if {@code false} - to the right
	 */
	public void setAlignToLeft(boolean alignToLeft) {
		this.alignToLeft = alignToLeft;
	}

}
