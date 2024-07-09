FROM fluent/fluentd:v1.12-1

COPY fluent.conf /fluentd/etc/

CMD ["fluentd", "-c", "/fluentd/etc/fluent.conf"]
