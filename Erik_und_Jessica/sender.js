#!/usr/bin/env node

var amqp = require("amqplib/callback_api");
var express = require("express");
var app = express();

app.get("/", function (req, res) {
  res.sendFile(__dirname + "/public/sender.html");
});

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

    app.get("/send", function (req, res) {
      var msg = "purchase";
      channel.sendToQueue(queue, Buffer.from(msg));
      console.log(" [x] Sent purchase event: %s", msg);
      res.json({ ok: true });
    });

    app.listen(3000);
    console.log("Sender bereit auf http://localhost:3000");
  });
});
