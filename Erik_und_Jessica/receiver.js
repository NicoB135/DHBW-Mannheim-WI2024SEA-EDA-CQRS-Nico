#!/usr/bin/env node

var amqp = require("amqplib/callback_api");
var express = require("express");
var app = express();

app.get("/", function (req, res) {
  res.sendFile(__dirname + "/public/receiver.html");
});

var stock = 100;
var clients = [];

amqp.connect("amqp://localhost", function (error0, connection) {
  if (error0) {
    throw error0;
  }
  connection.createChannel(function (error1, channel) {
    if (error1) {
      throw error1;
    }

    var queue = "lager_queue";

    channel.assertQueue(queue, {
      durable: false,
    });

    channel.consume(
      queue,
      function (msg) {
        console.log(" [x] Received: %s", msg.content.toString());

        stock--;
        console.log(" [x] Stock: %d", stock);

        clients.forEach(function (res) {
          res.write("data: " + JSON.stringify({ stock: stock }) + "\n\n");
        });

        channel.ack(msg);
      },
      {
        noAck: false,
      },
    );
  });
});

app.get("/events", function (req, res) {
  res.writeHead(200, {
    "Content-Type": "text/event-stream",
    "Cache-Control": "no-cache",
    Connection: "keep-alive",
    "Access-Control-Allow-Origin": "*",
  });

  res.write("data: " + JSON.stringify({ stock: stock }) + "\n\n");
  clients.push(res);

  req.on("close", function () {
    clients = clients.filter(function (c) {
      return c !== res;
    });
  });
});

app.listen(3001);
console.log("Receiver bereit auf http://localhost:3001");
