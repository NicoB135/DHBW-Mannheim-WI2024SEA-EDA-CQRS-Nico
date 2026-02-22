from ..events import AuctionCreated, BidPlaced, BidBeaten, AuctionEnded


class NotificationHandler:

    def on_auction_created(self, event: AuctionCreated) -> None:
        print(f"\n  [NEU] Auktion gestartet: '{event.title}'")
        print(f"        Startpreis: {event.starting_price:.2f}€")
        print(f"        Endet am:   {event.ends_at.strftime('%d.%m.%Y %H:%M')}")

    def on_bid_placed(self, event: BidPlaced) -> None:
        print(f"\n  [GEBOT] {event.bidder_name} bietet {event.amount:.2f}€")

    def on_bid_beaten(self, event: BidBeaten) -> None:
        print(f"  [INFO]  {event.previous_bidder}, du wurdest überboten!")
        print(f"          Dein Gebot: {event.previous_amount:.2f}€ → Neues Höchstgebot: {event.new_amount:.2f}€")

    def on_auction_ended(self, event: AuctionEnded) -> None:
        print(f"\n  [ENDE] Auktion '{event.title}' ist beendet!")
        if event.winner_name == "Kein Gebot":
            print(f"         Keine Gebote eingegangen.")
        else:
            print(f"         Gewinner: {event.winner_name} mit {event.winning_bid:.2f}€")
