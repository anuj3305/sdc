FROM onap/base_sdc-jetty:1.4.1

COPY chef-solo /root/chef-solo/

COPY chef-repo/cookbooks /root/chef-solo/cookbooks/

ADD WSSimulator*.war                    	${JETTY_BASE}/webapps/

USER root

RUN chown -R jetty:jetty               		${JETTY_BASE}/webapps

COPY startup.sh /root/

RUN chmod 777 /root/startup.sh

ENTRYPOINT [ "/root/startup.sh" ]
