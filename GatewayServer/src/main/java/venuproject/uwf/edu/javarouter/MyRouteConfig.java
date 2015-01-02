

package venuproject.uwf.edu.javarouter;

import org.apache.camel.builder.RouteBuilder;

public class MyRouteConfig extends RouteBuilder {

    @Override
    public void configure() {
        from("restlet:/persons?restletMethod=POST")
                .setBody(simple("insert into person(firstName, lastName) values('${header.firstName}','${header.lastName}')"))
                .to("jdbc:dataSource")
                .setBody(simple("select * from person where id in (select max(id) from person)"))
                .to("jdbc:dataSource");

        from("restlet:/persons/{personId}?restletMethods=GET,PUT,DELETE")
                .choice()
                    .when(simple("${header.CamelHttpMethod} == 'GET'"))
                        .setBody(simple("select * from person where id = ${header.personId}"))
                    .when(simple("${header.CamelHttpMethod} == 'PUT'"))
                        .setBody(simple("update person set firstName='${header.firstName}', lastName='${header.lastName}' where id = ${header.personId}"))
                    .when(simple("${header.CamelHttpMethod} == 'DELETE'"))
                        .setBody(simple("delete from person where id = ${header.personId}"))
                    .otherwise()
                        .stop()
                .end()
                .to("jdbc:dataSource");
        
        from("restlet:/persons?restletMethod=GET")
                .setBody(simple("select * from person"))
                .to("jdbc:dataSource");
    }

}
