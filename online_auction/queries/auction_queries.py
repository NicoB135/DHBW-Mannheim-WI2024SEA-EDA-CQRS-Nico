from ..handlers.auction_handler import AuctionHandler, AuctionState


class AuctionQueries:

    def __init__(self, auction_handler: AuctionHandler):
        self._auction_handler = auction_handler

    def get_current_highest_bid(self, auction_id: str) -> dict:
        auction = self._auction_handler.get_auction_state(auction_id)

        if auction is None:
            return {"error": f"Auktion '{auction_id}' nicht gefunden."}

        return {
            "auction_id": auction.auction_id,
            "title": auction.title,
            "current_price": auction.current_price,
            "current_bidder": auction.current_bidder or "Noch keine Gebote",
            "is_active": auction.is_active,
        }

    def get_bid_history(self, auction_id: str) -> list[dict]:
        auction = self._auction_handler.get_auction_state(auction_id)

        if auction is None:
            return []

        return [
            {"bidder": bidder, "amount": amount}
            for bidder, amount in auction.bid_history
        ]

    def get_all_active_auctions(self) -> list[dict]:
        return [
            {
                "auction_id": a.auction_id,
                "title": a.title,
                "current_price": a.current_price,
                "current_bidder": a.current_bidder or "Noch keine Gebote",
            }
            for a in self._auction_handler.get_all_auctions()
            if a.is_active
        ]
