# jikkenkichi
## MAKE WHEELS PROJECT

The project is first described here in plain text, more detailed information will come soon.

### machikouba(foundation tools)
>#### aop
>A simple annotation for automatic logging when methods are executed.
>#### fliter
>Add a prefix to service url path.
>Automatic logging of web requests and responses.
>Repackaging all responses, into the standard format specified in the framework.
>#### i18n
>A quick solution for multilingual situations.The language to be used can be specified using a simple header parameter.
>#### kfk
>Just a simple kafka demo now.MQ distribute center is on the way(if have time).
>#### zk
>A zookeeper toolset for distributed systems.
>Including configuration management,distributed lock,service discovery,circuit breakers.


### Seiran(Zookeeper-based "Spring Cloud + feign"-like light framework)
As a single-function package, the spring.factories is used directly so that it takes effect directly and automatically at startup.
#### Features:
>#### Service registration and discovery
>#### Load balancing
>#### Circuit Breakers
>#### Simplified service-to-service calls resolution
>
#### Introduction
>#### 1.Coding and prepare
>#### 1.1 Create 3 projects:contracts(nothing but contracts here),provider,customer.
>#### 1.2 About provider:
>#### Give the contract the simplest implementation.
>#### In main class,use @EnableNozomiZk to enable service registration,use @EnableNozomiFliter to enable the standard output format of the framework.
>#### 1.3 about customer:
>#### Implement contract,DO NOT need to write any code in method.Use @SeiranClient to set provider services info.
>#### In main class,use @EnableNozomiZk to enable service discovery.

>#### 2.Usage examples
>#### 2.1 Startup provider
>#### Provider will register itself in zookeeper.Let's startup 2 providers for test at first.
>#### 2.2 Startup customer
>#### SeiranScanner will find all @SeiranClient,then record all methods and providers used.
>#### All online providers' addresses will be synchronized to local from zookeeper,and keep continuous watching(like Eureka).
>#### Use a Scheduled method to make test request.
>#### Round-robin is used by default as a load balancing solution.
>#### HttpClient pool is used for reducing cost.
>#### 2.3 Add provider(s)
>#### Immediately after a new provider is started, customer receives a event notification from zookeeper.This new provider can then be used for round-robin sequence.
>#### 2.4 Reduce provider(s)
>#### After closing provider, the ephemeral node in zookeeper will automatically disappear after the timeout, i.e. as a notice to go offline.customer will remove the corresponding provider from the local record.
