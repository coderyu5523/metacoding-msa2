FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf
COPY order.html /usr/share/nginx/html/
COPY delivery.html /usr/share/nginx/html/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
