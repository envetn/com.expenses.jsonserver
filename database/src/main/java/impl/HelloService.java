package impl;

import services.Services;

/**
 * Created by lofie on 2017-09-12.
 */
public class HelloService implements Services
{
    @Override
    public String sayHello(String name)
    {
        return "Hello" + name;
    }
}
