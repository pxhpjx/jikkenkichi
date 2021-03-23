package org.nozomi.jikkenkichi.demo.contract.contracts;

import org.nozomi.jikkenkichi.demo.contract.pojo.ExampleRequest;
import org.nozomi.jikkenkichi.demo.contract.pojo.ExampleResponse;

//simple contract only, nothing that needs attention
public interface DemoContract {
    ExampleResponse reportCurrentTime();

    ExampleResponse makeRequestDouble(ExampleRequest request);

    ExampleResponse dummy();
}
