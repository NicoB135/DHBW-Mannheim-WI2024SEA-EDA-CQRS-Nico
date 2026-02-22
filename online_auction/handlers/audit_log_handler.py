from datetime import datetime
from ..events import AuctionCreated, BidPlaced, BidBeaten, AuctionEnded

LOG_FILE = "auction_audit.log"


class AuditLogHandler:

    def on_auction_created(self, event: AuctionCreated) -> None:
        self._write(f"AUCTION_CREATED | id={event.auction_id} | title={event.title} | start={event.starting_price:.2f}€")

    def on_bid_placed(self, event: BidPlaced) -> None:
        self._write(f"BID_PLACED      | id={event.auction_id} | bidder={event.bidder_name} | amount={event.amount:.2f}€")

    def on_bid_beaten(self, event: BidBeaten) -> None:
        self._write(f"BID_BEATEN      | id={event.auction_id} | loser={event.previous_bidder} | winner={event.new_bidder} | new_amount={event.new_amount:.2f}€")

    def on_auction_ended(self, event: AuctionEnded) -> None:
        self._write(f"AUCTION_ENDED   | id={event.auction_id} | winner={event.winner_name} | final={event.winning_bid:.2f}€")

    def _write(self, message: str) -> None:
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        with open(LOG_FILE, "a", encoding="utf-8") as log:
            log.write(f"[{timestamp}] {message}\n")
