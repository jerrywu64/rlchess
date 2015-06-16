Proposed code structure:


game:
    Board: Has a matrix containing pieces and methods for movement.
    [Piece]: Abstract class. Has images (black and white), a field for
            the piece's color, an abstract method for possible places
            that the piece can move to, and the piece's current location.
        Pawn, Knight, Bishop, Rook, Queen, King
    Game: Manages other aspects of the game, like history, captured
        pieces, turn count...

ai:
    //tbd


ui:
    //tbd

server:
    //tbd

