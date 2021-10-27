# Error handling with Either

**This solution brings consistency across all the layers, taking `Either` as only way to handle errors.** 

On common projects, in fact, we have `DataResult` from API ( Core ), sealed classes between Domain / Presentation, some `null` here, some exception there, some differnt type from various libraries ( like Store ) and some more other internal sealed classes ( probably in repository )



After all, this is not that different from what we already have, but more consistent.



## Other approaches

**Different approaches has information erasure about errors; but what does that mean?**

Taking an `Exception`, we generally see few types ( `NullPointer`, `IllegalState`, `IllegalArgument` ) but that says nothing without an error message, which is usually too much detailed ( maybe you call `getMessages`, but the exception says `adddress.key is null` ).



For this reason we have sealed classes as return type of use cases, but this is feasible when the code is not clean, when the use cases is doin too many things, beacuse if the use case only calls one function on the repository, cannot add much details about a possible error.



In this case should we move the sealed class down to the repository? But we will have a similar situation anyways.



## Conclusions

`Either` allows to have something similar, across all the layers, keeping the happy path ( `Right` ) separated from the errors ( `Left` ), with very powerful but simple operators that enable us to write code like errors don't exist, while we are already handling them, without even knowing it.

