import Layout from "./Layout.tsx";
import {useState, useEffect} from "react";
import './Menu.css'
import {Button} from "@mui/material";


interface MenuItem {
    id: string;
    name: string;
    description: string,
    price: number,
    image: string
}

function MenuItem(props: {item: MenuItem}) {


    const item = props.item


    const addToCartHandler = async () => {
        const response = await fetch('http://localhost:8080/add-to-cart', {
            method: "POST",
            mode: "cors",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({itemId: item.id})
        });
        const data = await response.json();
        console.log(data);
    }


    return (
        <div className={"menu-item"}>
            <p className={"item-header"}>{item.name} ({item.price})</p>
            <Button variant="outlined" onClick={(e)=> {
                console.log("will submit ", item.id, e)
                addToCartHandler()
            }}  >
                Add to Cart
            </Button>
            <p className={"description"}>{item.description}</p>
            <img src={item.image} alt={item.name} className={"menu-img"} />
        </div>
    )
}

function MenuDisplay() {
    const [loading, setLoading] = useState(true);
    const [menuItems, setMenu] = useState<MenuItem[]>([]);

    const fetchData = async () => {
        try {
            const response = await fetch('http://localhost:8080/sample-menu', {
                method: "GET",
                mode: "cors",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
            });
            const data = await response.json();
            console.log(data);
            setMenu(data)
        } catch (error) {
            console.error("Error fetching sample data", error);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        fetchData();
    }, []);

    let block = (<p>No Objects found</p>)

    if (loading) block = <div>Loading...</div>
    if (menuItems) block = <div>{
        menuItems.map((item: MenuItem) => {
            return <MenuItem key={item.id} item={item}/>
        })
    }</div>
    return block;

}

function TestSample() {


    return (
        <Layout>
            <div style={{padding: 20}}>
                <h2>Here's Our Menu. Select from an item below to begin ordering!</h2>
                <MenuDisplay/>
            </div>
        </Layout>
    )
}

export default TestSample;