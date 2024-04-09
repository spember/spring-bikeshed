import Layout from "./Layout.tsx";
import {Container} from "@mui/material";

export function Missing() {
    return (
        <Layout>
            <Container>
                <p>We're sorry this page is missing</p>
            </Container>
        </Layout>

    )
}