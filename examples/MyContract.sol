pragma solidity ^0.4.0;


contract SignedOffChainPactContract {

    mapping (address => mapping (address => mapping (address => bool))) public confirmedPacts;

    function addOwnedPendingPact(address one, address other, address pactId) public {
        require(one == msg.sender);
    }
}