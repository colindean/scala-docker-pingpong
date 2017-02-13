This is just me playing with Docker and using Scala to do it.

Build the app and the image:

    sbt assembly && docker build --tag whatever .

Start the server:

    docker run --publish-all=true --expose 4567 --hostname whatever-server whatever server 4567

Start the client after putting the listening port into `port`:

    docker run whatever client ${port} 172.17.0.1

If you use anything in here, you're probably foolish.