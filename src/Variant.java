/**
 Copyright 2016 Udey Rishi
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

/**
 * An extension interface of the {@link Operation} for operations that can be used as variants in the
 * Recovery Blocks fault tolerant algorithm implemented in the {@link RecoveryBlocksExecutor}.
 * Created by rishi on 2016-02-20.
 */
public interface Variant<T> extends Operation<T> {
    /**
     * Returns a string descriptor for this variant.
     * @return A string descriptor for this variant.
     */
    String getName();
}
