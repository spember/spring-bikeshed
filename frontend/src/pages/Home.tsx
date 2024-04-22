import Layout from "./Layout.tsx";
import {useState, useEffect} from "react";


interface UserDetails {
    id: object,
    name: string,
}
function UserSalute() {

    const [userDetails, setUserDetails] = useState<null | UserDetails>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string|null>(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch('http://localhost:8080/user', {
                    method: "GET",
                    mode: "cors", // no-cors, *cors, same-origin
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },
                });
                const data = await response.json();
                console.log("Got data", data);
                setUserDetails(data);

            } catch (error) {
                console.log(typeof error)
                console.error("Error fetching user details", error);
                setError("error");
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (loading) {
        return <p>Fetching User Details...</p>;
    }
    if (error) {
        return <p>Error fetching User Details: {error}</p>;
    }
    return (
        <p>Hello, {userDetails?.name}</p>
    );
}


function Home() {
    return (
        <Layout>
            <div style={{padding: 20}}>
                <h2>Welcome to the BikeShed</h2>
                <UserSalute/>
                <p>Let's learn about events</p>
            </div>
        </Layout>
    );
}

export default Home;