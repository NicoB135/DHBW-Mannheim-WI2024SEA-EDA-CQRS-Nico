from ..commands import CreateAuction, PlaceBid, EndAuction
from ..events import AuctionCreated, BidPlaced, BidBeaten, AuctionEnded
from ..core.event_bus import EventBus


class AuctionState:

    def __init__(self, auction_id: str, title: str, starting_price: float):
        self.auction_id = auction_id
        self.title = title
        self.current_price = starting_price
        self.current_bidder: str | None = None
        self.is_active = True
        self.bid_history: list[tuple[str, float]] = []


class AuctionHandler:

    def __init__(self, event_bus: EventBus):
        self._event_bus = event_bus
        self._auctions: dict[str, AuctionState] = {}

    def handle_create_auction(self, command: CreateAuction) -> None:
        auction = AuctionState(command.auction_id, command.title, command.starting_price)
        self._auctions[command.auction_id] = auction

        self._event_bus.publish(AuctionCreated(
            auction_id=command.auction_id,
            title=command.title,
            starting_price=command.starting_price,
            ends_at=command.ends_at,
        ))

    def handle_place_bid(self, command: PlaceBid) -> None:
        auction = self._auctions.get(command.auction_id)

        if auction is None:
            raise ValueError(f"Auktion '{command.auction_id}' existiert nicht.")
        if not auction.is_active:
            raise ValueError(f"Auktion '{command.auction_id}' ist bereits beendet.")
        if command.amount <= auction.current_price:
            raise ValueError(
                f"Gebot {command.amount}€ muss höher sein als aktuelles Höchstgebot {auction.current_price}€."
            )

        previous_bidder = auction.current_bidder
        previous_amount = auction.current_price

        auction.bid_history.append((command.bidder_name, command.amount))
        auction.current_price = command.amount
        auction.current_bidder = command.bidder_name

        self._event_bus.publish(BidPlaced(
            auction_id=command.auction_id,
            bidder_name=command.bidder_name,
            amount=command.amount,
        ))

        if previous_bidder is not None:
            self._event_bus.publish(BidBeaten(
                auction_id=command.auction_id,
                previous_bidder=previous_bidder,
                previous_amount=previous_amount,
                new_bidder=command.bidder_name,
                new_amount=command.amount,
            ))

    def handle_end_auction(self, command: EndAuction) -> None:
        auction = self._auctions.get(command.auction_id)

        if auction is None:
            raise ValueError(f"Auktion '{command.auction_id}' existiert nicht.")

        auction.is_active = False

        self._event_bus.publish(AuctionEnded(
            auction_id=command.auction_id,
            title=auction.title,
            winner_name=auction.current_bidder or "Kein Gebot",
            winning_bid=auction.current_price,
        ))

    def get_auction_state(self, auction_id: str) -> AuctionState | None:
        return self._auctions.get(auction_id)

    def get_all_auctions(self) -> list[AuctionState]:
        return list(self._auctions.values())
