	The project is first described here in plain text, more detailed information will come soon.

# Introduction

## 🔧machikouba(foundation toolset)
###Description by package
- aop

	Simple annotations for automatic debugging when methods are executed.
- **fliter**

	Add a prefix to service url path.

	Automatic logging of web requests and responses.

	**Repackaging all responses, into the standard format specified in this framework.**
- i18n

	A quick solution for multilingual situations.The language to be used can be specified using a simple header parameter.
- kfk

	Just a simple kafka demo now.MQ distribute center is on the way(if have time).
- **zk**

	A zookeeper toolset for distributed systems.
	
	Including **configuration management,distributed lock,service registration**.


## ✈️Seiran(Zookeeper-based "Spring Cloud + feign"-like light framework)
	As a single-function package, the spring.factories is used directly so that it takes effect directly and automatically at startup.
### Features

-	Service registration and discovery

-	Load balancing

-	Circuit Breakers

-	Simplified service-to-service calls resolution

### A brief note on the code and how it is used
	(some images are on the way)
- **Code description (detailed than above)**
 
	1. Let's look at all the dependencies used throughout the project(for easy viewing,dependencies exists only in the main project's pom). As an experimental project, try to use more of my own implementation and less dependencies.
	
	2. As with many distributed projects, zookeeper is used here as the tool for **service registration and discovery**. `ZkOnline(machikouba)` ensures that services are registered as soon as they are online(includes reconnects). `SeiranScanner` will automatically scan for all clients with `@SeiranClient` when the program starts,record all provider and method information,and makes watches for discovery.
	
	3. When the method annotated with `@SeiranRequest` is executed,`SeiranAspect` will work. Benefit from the proxy offered by AspectJ,the methods in the client are only used as symbols and do not really need to implement the code,`SeiranAspect` will automatically extract the url to be requested based on the method,**simplified service-to-service calls resolution** is thus implemented.All found providers will be used efficiently and Round-Robin will be used as a solution for **load balancing** at the time of request. In case of unexpected network errors, the lightweight **circuit breakers** based on time judgments will also take effect. HttpClient pool is used for reducing cost.

- **Testing Preparation**
	1. Create 3 projects:contracts(nothing but contracts here),provider,customer.
	
	2. About provider:
	
		Give the contract the simplest implementation.

		In main class,use `@EnableNozomiZk` to enable service registration,use `@EnableNozomiFliter` to enable the standard output format of the framework.
	3. About customer:
	
		Implement contract,DO NOT need to write any code in method.Use `@SeiranClient` to set provider services info.

		In main class,use `@EnableNozomiZk` to enable service discovery.

- **Practical use examples**
	1. Startup provider
	
		Provider will register itself in zookeeper.Let's startup 2 providers for test at first.
	2. Startup customer
	 
		`SeiranScanner` will find all `@SeiranClient`,then record all methods and providers used.
		All online providers' addresses will be synchronized to local from zookeeper,and keep continuous watching(like Eureka).

		Use a Scheduled method to make test request.We can see that Round-Robin is used by default as a load balancing solution.

	3. Add provider(s)
	
		Immediately after a new provider is started, customer receives a event notification from zookeeper. This new provider can then be used for Round-Robin sequence.
	4. Reduce provider(s)
	
		After closing a provider, the ephemeral node for registration in zookeeper will automatically disappear after the timeout, i.e. as a notice to go offline. Customer will remove the corresponding provider from the local record.
	5. Try a mistake url
	
		We can never get the right response from "/wrong-path". After the threshold is reached, it will be temporarily and automatically deactivated.After the recovery time is reached, it will be enabled again.Since no further strategy implementation is done, this process will keep cycling.
