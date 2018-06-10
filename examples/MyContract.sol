pragma solidity ^0.4.0;


contract SignedOffChainPactContract {

    mapping (address => mapping (address => mapping (address => bool))) public confirmedPacts;

    function addOwnedPendingPact(address one, address other, address pactId) public {
        require(one == msg.sender);
    }

    function addOwnedPendingPact2(address one, address other, uint pactId) public {
        require(one == msg.sender);
    }

    function baz(uint32 x, bool y) returns (bool r) { r = x > 32 || y; }

}